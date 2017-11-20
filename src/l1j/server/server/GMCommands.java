/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */

package l1j.server.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javolution.util.FastMap;
import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.SpecialEventHandler;
import l1j.server.GameSystem.Boss.BossSpawnTimeController;
import l1j.server.server.TimeController.DevilController;
import l1j.server.server.TimeController.HellDevilController;
import l1j.server.server.TimeController.WarTimeController;
import l1j.server.server.TimeController.WitsTimeController;
import l1j.server.server.command.L1Commands;
import l1j.server.server.command.executor.L1CommandExecutor;
import l1j.server.server.datatables.AutoLoot;
import l1j.server.server.datatables.BoardTable;
import l1j.server.server.datatables.CastleTable;
import l1j.server.server.datatables.ClanTable;
import l1j.server.server.datatables.DropTable;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.datatables.HouseTable;
import l1j.server.server.datatables.IpTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.ModelSpawnTable;
import l1j.server.server.datatables.NpcShopTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.PolyTable;
import l1j.server.server.datatables.ShopTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1War;
import l1j.server.server.model.L1WarSpawn;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1EventCrownInstance;
import l1j.server.server.model.Instance.L1EventTowerInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_Ability;
import l1j.server.server.serverpackets.S_Chainfo;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_Test;
import l1j.server.server.serverpackets.S_War;
import l1j.server.server.templates.L1Command;
import l1j.server.server.templates.L1House;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.utils.CommonUtil;
import l1j.server.server.utils.IntRange;
import l1j.server.server.utils.SQLUtil;
import server.manager.eva;
import server.system.autoshop.AutoShop;
import server.system.autoshop.AutoShopManager;

// Referenced classes of package l1j.server.server:
// ClientThread, Shutdown, IpTable, MobTable,
// PolyTable, IdFactory

public class GMCommands {

	private static Logger _log = Logger.getLogger(GMCommands.class.getName());

	private static GMCommands _instance;

	private GameServerSetting _GameServerSetting;

	private GMCommands() {
	}

	public static GMCommands getInstance() {
		if (_instance == null) {
			_instance = new GMCommands();
		}
		return _instance;
	}

	private String complementClassName(String className) {
		if (className.contains(".")) {
			return className;
		}
		return "l1j.server.server.command.executor." + className;
	}

	private boolean executeDatabaseCommand(L1PcInstance pc, String name, String arg) {
		try {
			L1Command command = L1Commands.get(name);
			if (command == null) {
				return false;
			}
			if (pc.getAccessLevel() < command.getLevel()) {
				pc.sendPackets(new S_ServerMessage(74, "[Command] Command " + name)); 
				return true;
			}
			Class<?> cls = Class.forName(complementClassName(command
					.getExecutorClassName()));
			L1CommandExecutor exe = (L1CommandExecutor) cls.getMethod(
					"getInstance").invoke(null);
			exe.execute(pc, name, arg);
			eva.LogCommandAppend(pc.getName(), name, arg);
			return true;
		} catch (Exception e) {
			_log.log(Level.SEVERE, "error gm command", e);
		}
		return false;
	}

	public void handleCommands(L1PcInstance gm, String cmdLine) {

		StringTokenizer token = new StringTokenizer(cmdLine);
		String cmd = token.nextToken();
		String param = "";
		while (token.hasMoreTokens()) {
			param = new StringBuilder(param).append(token.nextToken()).append(
					' ').toString();
		}
		param = param.trim();

		if (executeDatabaseCommand(gm, cmd, param)) { 
			if (!cmd.equalsIgnoreCase("redo")) {
				_lastCommands.put(gm.getId(), cmdLine);
			}
			return;
		}
		if (gm.getAccessLevel() != Config.GMCODE) {
			gm.sendPackets(new S_ServerMessage(74, "[Command] Command " + cmd));
			return;
		}
		eva.LogCommandAppend(gm.getName(), cmd, param);
		

	}

	private void spawnmodel(L1PcInstance gm, String param) {
		StringTokenizer st = new StringTokenizer(param);
		int type = Integer.parseInt(st.nextToken(), 10);
		ModelSpawnTable.getInstance().insertmodel(gm, type);
		gm.sendPackets(new S_SystemMessage("[Command] ï¿½ï¿½ ï¿½Ö¾ï¿½ï¿½ï¿½"));
	}

	private void showHelp(L1PcInstance pc) {
		S_ChatPacket s_chatpacket = new S_ChatPacket(pc, ""
				, Opcodes.S_OPCODE_MSG, 11);
			pc.sendPackets(s_chatpacket);
	}

	private void paket(L1PcInstance gm, String param) {
		try {
			StringTokenizer st = new StringTokenizer(param);
			int id = Integer.parseInt(st.nextToken(), 10);			

			gm.sendPackets(new S_PacketBox(id));
		} catch (Exception exception) {
			gm.sendPackets(new S_SystemMessage("[Command] ."));
		}
	}
	
	

	private void autoloot(L1PcInstance gm, String param) {
		try {
			StringTokenizer tok = new StringTokenizer(param);
			String type = tok.nextToken();
			if (type.equalsIgnoreCase("autoloot")) {
				AutoLoot.getInstance().reload();
				gm.sendPackets(new S_SystemMessage("autoloot."));
			} else if (type.equalsIgnoreCase("ï¿½Ë»ï¿½")) {
				java.sql.Connection con = null;
				PreparedStatement pstm = null;
				ResultSet rs = null;

				String nameid = tok.nextToken();
				try {
					con = L1DatabaseFactory.getInstance().getConnection();
					String strQry;
					strQry = " Select e.item_id, e.name from etcitem e, autoloot l where l.item_id = e.item_id and name Like '%"
							+ nameid + "%' ";
					strQry += " union all "
							+ " Select w.item_id, w.name from weapon w, autoloot l where l.item_id = w.item_id and name Like '%"
							+ nameid + "%' ";
					strQry += " union all "
							+ " Select a.item_id, a.name from armor a, autoloot l where l.item_id = a.item_id and name Like '%"
							+ nameid + "%' ";
					pstm = con.prepareStatement(strQry);
					rs = pstm.executeQuery();
					while (rs.next()) {
						gm.sendPackets(new S_SystemMessage("["
								+ rs.getString("item_id") + "] "
								+ rs.getString("name")));
					}
				} catch (Exception e) {
				} finally {
					rs.close();
					pstm.close();
					con.close();
				}
			} else {
				String nameid = tok.nextToken();
				int itemid = 0;
				try {
					itemid = Integer.parseInt(nameid);
				} catch (NumberFormatException e) {
					itemid = ItemTable.getInstance()
							.findItemIdByNameWithoutSpace(nameid);
					if (itemid == 0) {
						gm.sendPackets(new S_SystemMessage(
								"ccccccc. "));
						return;
					}
				}

				L1Item temp = ItemTable.getInstance().getTemplate(itemid);
				if (temp == null) {
					gm.sendPackets(new S_SystemMessage("dddddddd. "));
					return;
				}
				if (type.equalsIgnoreCase("ccccccccccc")) {
					if (AutoLoot.getInstance().isAutoLoot(itemid)) {
						gm.sendPackets(new S_SystemMessage("eeeeeeeeee."));
						return;
					}
					AutoLoot.getInstance().storeId(itemid);
					gm.sendPackets(new S_SystemMessage("aaaaaaaaaaaaaaaaaaaa"));
				} else if (type.equalsIgnoreCase("bcbcbc")) {
					if (!AutoLoot.getInstance().isAutoLoot(itemid)) {
						gm.sendPackets(new S_SystemMessage("bcbcbc."));
						return;
					}
					gm.sendPackets(new S_SystemMessage("bcbcbcbc."));
					AutoLoot.getInstance().deleteId(itemid);
				}
			}
		} catch (Exception e) {
			gm.sendPackets(new S_SystemMessage("bdbdbdbd"));
			gm.sendPackets(new S_SystemMessage("bdbdbdbd itemid|name"));
			gm.sendPackets(new S_SystemMessage(".bdbdbdbd name"));
		}
	}

	private void witsGameStart(L1PcInstance pc, String param) {
		try {
			StringTokenizer tok = new StringTokenizer(param);
			int witsCount = Integer.parseInt(tok.nextToken());

			WitsTimeController.getInstance().startcheckChatTime(witsCount);
			pc.sendPackets(new S_SystemMessage("witsGameStart "));

		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".witsGameStart [witsGameStart]"));
		}
	}
	
	private void entertime(L1PcInstance pc) {
		try {
			int entertime1 = 180 - pc.getGdungeonTime() % 1000;
			int entertime2 = 300 - pc.getLdungeonTime() % 1000;
			int entertime3 = 120 - pc.getTkddkdungeonTime() % 1000;
			int entertime4 = 120 - pc.getDdungeonTime() % 1000;
			int entertime5 = 120 - pc.getoptTime() % 1000;
			   
			String time1 = Integer.toString(entertime1);
			String time2 = Integer.toString(entertime2);
			String time3 = Integer.toString(entertime3);
			String time4 = Integer.toString(entertime4);
			String time5 = Integer.toString(entertime5);
			
			
			pc.sendPackets(new S_ServerMessage(2535, "\\fYentertime1", time1)); 
			pc.sendPackets(new S_ServerMessage(2535, "\\fYentertime2", time2));
			pc.sendPackets(new S_ServerMessage(2535, "\\fYentertime3", time3));
			pc.sendPackets(new S_ServerMessage(2535, "\\fYentertime4", time4));
			pc.sendPackets(new S_ServerMessage(2535, "\\fYentertime5", time5));
		} catch (Exception e) {
		}
	}
	private void AllPlayerList(L1PcInstance gm, String param) {
		try {
			int SearchCount = 0;
			AutoShopManager shopManager = AutoShopManager.getInstance();
			AutoShop autoshop = null;

			gm.sendPackets(new S_SystemMessage("\\fY----------------------------------------------------"));
			for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
				try {
					if (pc == null || pc.getNetConnection() == null || pc.noPlayerCK || pc.isPrivateShop()) {
						continue;
					}
					autoshop = shopManager.getShopPlayer(pc.getName());
					if (!pc.noPlayerCK && autoshop == null) {
						gm.sendPackets(new S_SystemMessage("\\fU·¹º§ : " + pc.getLevel() + ", Ä³¸¯¸í : " + pc.getName() + ", °èÁ¤ : " + pc.getAccountName()));
						SearchCount++;
					}
				} catch (Exception e) {
				}
			}
			gm.sendPackets(new S_SystemMessage("\\fY" + SearchCount + "¸íÀÇ »ç¿ëÀÚ°¡ ÀÖ½À´Ï´Ù."));
			gm.sendPackets(new S_SystemMessage("\\fY----------------------------------------------------"));
		} catch (Exception e) {
			gm.sendPackets(new S_SystemMessage(".AllPlayerList"));
		}
	}
	
	private void InventoryDelete(L1PcInstance pc, String param) {
		try {
			
			for (L1ItemInstance item : pc.getInventory().getItems()) {
				if (!item.isEquipped()) {
					pc.getInventory().removeItem(item);
				}
			}
			
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".InventoryDelete"));
		}
	}
	private void GmCharacterNameChange(L1PcInstance pc, String param) {
		try {
			StringTokenizer tok = new StringTokenizer(param);
			String name = "";
			try {
				name = tok.nextToken();
			} catch (Exception e) {
				name = "";
			}

			pc.setCharacterName(name);
			L1Teleport.teleport(pc, pc.getX(), pc.getY(), pc.getMapId(), pc.getMoveState().getHeading(), false);
			pc.sendPackets(new S_SystemMessage("\\fYÄ³¸¯¸í " + name + "º¯°æµÇ¾ú½À´Ï´Ù."));
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".GmCharacterNameChange [Ä³¸¯¸í]"));
		}
	}
	

	private void LargeAreaIPBan(L1PcInstance pc, String param) {		
		try {
			StringTokenizer st = new StringTokenizer(param);
					
			String charName = st.nextToken();
			String banIp = "";
			
			L1PcInstance player = L1World.getInstance().getPlayer(charName);
			
			if (player != null) {
				banIp = player.getNetConnection().getIp();
				
				String[] banIpArr = banIp.split("\\.");
				
				IpTable iptable = IpTable.getInstance();
				pc.sendPackets(new S_SystemMessage("----------------------------------------------------"));		
				Account.ban(player.getAccountName());
				player.logout();
				player.getNetConnection().kick();
				for (int i = 1; i <= 255; i++) {
					iptable.banIp(banIpArr[0] + "." + banIpArr[1] + "." + banIpArr[2] + "." + i);
				}
			
				pc.sendPackets(new S_SystemMessage("IP: " + banIpArr[0] + "." + banIpArr[1] + "." + banIpArr[2] + ".1~255 ´ë¿ªÀÌ Â÷´ÜµÇ¾ú½À´Ï´Ù."));
				pc.sendPackets(new S_SystemMessage("----------------------------------------------------"));			
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".LargeAreaIPBan  [ip]"));	
		} 
	}
	
	private void LargeAreaBan(L1PcInstance pc, String param) {		
		try {
			StringTokenizer st = new StringTokenizer(param);
					
			int range = Integer.parseInt(st.nextToken());
			int count = 0;
			IpTable iptable = IpTable.getInstance();
			pc.sendPackets(new S_SystemMessage("----------------------------------------------------"));
			for (L1PcInstance player : L1World.getInstance().getVisiblePlayer(pc, range)) {
				Account.ban(player.getAccountName()); // ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ BANï¿½ï¿½Å²ï¿½ï¿½.
				iptable.banIp(player.getNetConnection().getIp()); // BAN ï¿½ï¿½ï¿½ï¿½Æ®ï¿½ï¿½ IPï¿½ï¿½ ï¿½ß°ï¿½ï¿½Ñ´ï¿½.
				pc.sendPackets(new S_SystemMessage(player.getName() + ", (" + player.getAccountName() + ")"));
				player.logout();
				player.getNetConnection().kick();
				count++;
			}
			pc.sendPackets(new S_SystemMessage("LargeAreaBan " + count + "xxxxxxxxxxxxxxxxx."));
			pc.sendPackets(new S_SystemMessage("----------------------------------------------------"));			
			
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".LargeAreaBan  [cccccccc]"));	
		} 
	}
	
	private void search_banned(L1PcInstance paramL1PcInstance) {
	    try  {
	      String str1 = null;
	      String str2 = null;
	      int i = 0;
	      Connection localConnection = null;
	      localConnection = L1DatabaseFactory.getInstance().getConnection();
	      PreparedStatement localPreparedStatement = null;
	      localPreparedStatement = localConnection.prepareStatement("select accounts.login, characters.char_name from accounts,characters where accounts.banned=1 and accounts.login=characters.account_name ORDER BY accounts.login ASC");
	      ResultSet localResultSet = localPreparedStatement.executeQuery();
	      while (localResultSet.next()) {
	        str1 = localResultSet.getString(1);
	        str2 = localResultSet.getString(2);
	        paramL1PcInstance.sendPackets(new S_SystemMessage(new StringBuilder().append("°èÁ¤¸í:[").append(str1).append("], Ä³¸¯¸í:[").append(str2).append("]").toString()));
	        ++i;
	      }
	      localResultSet.close();
	      localPreparedStatement.close();
	      localConnection.close();
	      paramL1PcInstance.sendPackets(new S_SystemMessage(new StringBuilder().append("Ä³¸¯¸í [").append(i).append("]Â÷´Ü.").toString()));
	    } catch (Exception localException)  {
	    }
	  }

	private void stopWar(L1PcInstance gm, String param) {
		try {
			StringTokenizer tok = new StringTokenizer(param);
			String name = tok.nextToken();

			WarTimeController.getInstance().stopWar(name);
			L1World.getInstance().broadcastPacketToAll(
					new S_SystemMessage("\\fYname + stopWar"));
		} catch (Exception e) {
			gm.sendPackets(new S_SystemMessage(".stopWar [stopWar]"));
		}
	}/// ï¿½ß°ï¿½

	public void bugment(L1PcInstance pc, String param) {
		if (param.equalsIgnoreCase("bugment1")) {
			pc.sendPackets(new S_SystemMessage("[!] bugment1."));
			pc.isbugment(false);
		} else if (param.equalsIgnoreCase("bugment2")) {
			pc.sendPackets(new S_SystemMessage("[!] bugment2."));
			pc.isbugment(true);
		} else {
			pc.sendPackets(new S_SystemMessage("\\fY[bugment] .bugment (bugment)or(bugment)"));
			if (!pc.isbugment()) {
				pc.sendPackets(new S_SystemMessage("bugment : [OFF]"));
			} else {
				pc.sendPackets(new S_SystemMessage("bugment : [ON]"));
			}
		}
	}
	
	private void maphack(L1PcInstance gm, String cmdName) {
		  try {
		   StringTokenizer tok = new StringTokenizer(cmdName);
		   String onoff = tok.nextToken();
		   if(onoff.equals("ÄÔ")){
		    gm.sendPackets(new S_Ability(3, true));
		   }else if(onoff.equals("²û")){
		    gm.sendPackets(new S_Ability(3, false));
		   }
		  } catch (Exception e) {
		   gm.sendPackets(new S_SystemMessage(".¸ÊÇÙ [ÄÔ or ²û]"));
		  }
		 }
	
	private void clanmark(L1PcInstance pc) {
		try {
			int i = 1;
			if (pc.GMCommand_Clanmark) {
				i = 3;
				pc.GMCommand_Clanmark = false;
			} else
				pc.GMCommand_Clanmark = true;
			for (L1Clan clan : L1World.getInstance().getAllClans()) {
				if (clan != null) {
					pc.sendPackets(new S_War(i, pc.getClanname(), clan
							.getClanName()));
				}
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage("[Command] .Ç÷¸¶Å©"));
		}
	}

	private void CallClan(L1PcInstance pc, String param) {
		try {
			StringTokenizer st = new StringTokenizer(param);
			String clanname = st.nextToken();
			L1Clan clan = L1World.getInstance().getClan(clanname);
			if (clan != null) {
				for (L1PcInstance player : clan.getOnlineClanMember()) {
					if (!player.isPrivateShop() && !player.isFishing()) {
						L1Teleport.teleportToTargetFront(player, pc, 2); 
					}
				}
				pc.sendPackets(new S_SystemMessage("[ " + clanname
						+ " ] CallClan1."));
			} else {
				pc.sendPackets(new S_SystemMessage("[ " + clanname
						+ " ] CallClan2."));
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".CallClan [Ä³¸¯??] ¼ÒÈ¯??"));
		}
	}

	public void dlqpsxmtmvhs(L1PcInstance gm, String arg) {
	}

	private void Gmspawn(int npcId, int x, int y, short mapid, int heading) {
		try {
			L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(npcId);
			npc.setId(ObjectIdFactory.getInstance().nextId());
			npc.setMap(mapid);
			npc.setX(x);
			npc.setY(y);
			npc.setHomeX(npc.getX());
			npc.setHomeY(npc.getY());
			npc.getMoveState().setHeading(heading);
			L1World.getInstance().storeObject(npc);
			L1World.getInstance().addVisibleObject(npc);

		} catch (Exception e) {
		}
	}

	private static void delenpc(L1PcInstance gm, int npcid) {
		L1NpcInstance npc = null;
		for (L1Object object : L1World.getInstance().getObject()) {
			if (object instanceof L1NpcInstance) {
				npc = (L1NpcInstance) object;
				if (npc.getNpcTemplate().get_npcId() == npcid) {
					npc.deleteMe();
					gm.sendPackets(new S_SystemMessage("npc delete."));
					npc = null;
				}
			}
		}
	}

	private void selingChange(L1PcInstance pc, String param) {
	}

	private static Map<Integer, String> _lastCommands = new FastMap<Integer, String>();

	private void redo(L1PcInstance pc, String arg) {
	}

	private void rate(L1PcInstance gm, String param) {
	}

	private void allpresent(L1PcInstance gm, String param) {
	}

	private void usersummon(L1PcInstance pc, String param) {
		try {
			StringTokenizer tok = new StringTokenizer(param);
			String user = tok.nextToken();
			String idString = tok.nextToken();
			String nmString = tok.nextToken();

			L1PcInstance player = L1World.getInstance().getPlayer(user);

			if (player != null) {
				int npcId = Integer.parseInt(idString);
				int npcNm = Integer.parseInt(nmString);

				for (int i = 0; i < npcNm; i++) {
					L1Npc npc = NpcTable.getInstance().getTemplate(npcId);
					L1SummonInstance summonInst = new L1SummonInstance(npc,player);
					summonInst.setPetcost(0);
				}
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".npc NPCID npc"));
		}
	}

	private void boardDel(L1PcInstance pc, String param) {
		try {
			StringTokenizer st = new StringTokenizer(param);
			int id = Integer.parseInt(st.nextToken(), 10);
			BoardTable.getInstance().deleteTopic(id);
		} catch (Exception exception) {pc.sendPackets(new S_SystemMessage("[Command] .°Ô½ÃÆÇ»èÁ¦ [id] aaa"));
		}
	}

	private static boolean CheckPc(L1PcInstance pc, String arg) {
		L1PcInstance target = L1World.getInstance().getPlayer(arg);
		if (pc.isGhost())
			return true;
		if (pc.getOnlineStatus() == 0)
			return true;
		if (pc.getOnlineStatus() != 1)
			return true;
		if (!pc.isGm() && pc.isInvisble()) {
			pc.sendPackets(new S_ServerMessage(334));
			return true;
		}
		if (pc.getAccountName().equalsIgnoreCase(target.getAccountName())) {
			pc.sendPackets(new S_Disconnect());
			target.sendPackets(new S_Disconnect());
			return true;
		}
		if (pc.getId() == target.getId()) {
			pc.getNetConnection().kick();
			pc.getNetConnection().close();
			target.getNetConnection().kick();
			target.getNetConnection().close();
			return true;
		} else if (pc.getId() != target.getId()) {
			if (pc.getAccountName().equalsIgnoreCase(target.getAccountName())) {
				if (!target.isPrivateShop()) {
					pc.getNetConnection().kick();
					pc.getNetConnection().close();
					target.getNetConnection().kick();
					target.getNetConnection().close();
					return true;
				}
			}
		}
		return false;
	}


}
