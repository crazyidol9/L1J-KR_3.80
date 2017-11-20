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
package l1j.server.server.model.Instance;

import java.util.Random;
import java.util.logging.Logger;
import java.util.logging.Level;

import javolution.util.FastTable;
import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.ObjectIdFactory;
import l1j.server.server.Opcodes;
import l1j.server.server.datatables.DropTable;
import l1j.server.server.datatables.NPCTalkDataTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.UBTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.CharPosUtil;
import l1j.server.server.model.Dead;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1MobSkillUse;
import l1j.server.server.model.L1NpcDeleteTimer;
import l1j.server.server.model.L1NpcTalkData;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1UltimateBattle;
import l1j.server.server.model.L1World;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_NPCPack;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.utils.CalcExp;
import l1j.server.server.utils.L1SpawnUtil;

public class L1MonsterInstance extends L1NpcInstance {

	private static final long serialVersionUID = 1L;

	private static Logger _log = Logger.getLogger(L1MonsterInstance.class
			.getName());

	public static int[][] _classGfxId = { { 0, 1 }, { 48, 61 }, { 37, 138 },
			{ 734, 1186 }, { 2786, 2796 }, { 6658, 6661 }, { 6671, 6650 } };

	private static Random _random = new Random(System.nanoTime());

	private int _storeDroped;
	
	private boolean seeon = false;// ��Ÿ��

	private Dead dead = new Dead(this, null);

	private L1MobSkillUse mob;

	private L1NpcInstance _attacker = null;

	private int hprsize;

	@Override
	public void onItemUse() {
		if (!isActived() && _target != null) {
			if (getLevel() <= 45) {
				useItem(USEITEM_HASTE, 40);

             }
			try {
                if (getNpcTemplate().get_npcId() == 45590) { // ���Ǿ� ��ȣ
				String chat = "�׸����۴Կ��� �帱 �����̷α���.";
				Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
						chat, 0));
			}else if (getNpcTemplate().get_npcId() == 45513) { // ���Ǿ� ��ȣ
				String chat = "���ҷӱ��� ����������!";
				Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
						chat, 0));
			}else if (getNpcTemplate().get_npcId() == 46140) { //�ó�
				String chat = "���մ��� ȥ���� ������ ���� ����!";
				Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
						chat, 0));
			}else if (getNpcTemplate().get_npcId() == 46141) { // ����
				String chat = "�װ� ���Ѵ�. �� �ڸ����� ��� �׾��";
				Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
						chat, 0));			
			}else if (getNpcTemplate().get_npcId() == 46142) { //����
				String chat = "������ �ΰ����̿�, ��� �׿��ָ�!";
				Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
						chat, 0));
			}else if (getNpcTemplate().get_npcId() == 45547) { // ���Ǿ� ��ȣ
				String chat = "�� �̳��! �� ����� ��� �쿩�����״�!";
				Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
						chat, 0));
			}else if (getNpcTemplate().get_npcId() == 45606) { // ���Ǿ� ��ȣ
				String chat = "���� ���� ���� ����!";
				Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
						chat, 0));
			}else if (getNpcTemplate().get_npcId() == 45650) { // ���Ǿ� ��ȣ
				String chat = "����..�뿹 ������ �ǹ�������! �׾��!";
				Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
						chat, 0));
			}else if (getNpcTemplate().get_npcId() == 45652) { // ���Ǿ� ��ȣ
				String chat = "��..��..���� �������..";
				Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
						chat, 0));
			}else if (getNpcTemplate().get_npcId() == 45653) { // ���Ǿ� ��ȣ
				String chat = "�� ��鿡�� Ư���� ������ ������ ������ ���ָ�!";
				Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
						chat, 0));
			}else if (getNpcTemplate().get_npcId() == 45654) { // ���Ǿ� ��ȣ
				String chat = "���Լ� ����ĥ �� ���� �� ������!";
				Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
						chat, 0));
			}else if (getNpcTemplate().get_npcId() == 45618) { // ���Ǿ� ��ȣ
				String chat = "���� �����ϱ���!";
				Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
						chat, 0));
			}else if (getNpcTemplate().get_npcId() == 45672) { // ���Ǿ� ��ȣ
				String chat = "������ ���� �����ָ�! ���ߺ��ƶ�!";
				Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
						chat, 0));
			}else if (getNpcTemplate().get_npcId() == 45673) { // ���Ǿ� ��ȣ
				String chat = "������ ��� ������ �����ϴ°�!!!";
				Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
						chat, 0));
			}else if (getNpcTemplate().get_npcId() == 4036016){ 
				String chat = "���� ���� : ������������....����������....";
				Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
			}else if (getNpcTemplate().get_npcId() == 4036017){
				String chat = "���� ���� : ����������....����������...";
				Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
			}else if (getNpcTemplate().get_npcId() == 400016){
				String chat = "�ƴ��� : ���񿡰� ������....";
				Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
			}else if (getNpcTemplate().get_npcId() == 400017){
				String chat = "ȣ�罺 : �ں�� ����....";
				Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
			} else if (getNpcTemplate().get_npcId() == 4039020) {
				String chat = "��Ÿ�� : ���� ���Ⱑ �����! ����� �ΰ����̶�...";
				Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
			}else if (getNpcTemplate().get_npcId() == 4039021) { // ���� ��Ÿ�� 2��
				String chat = "��Ÿ�� : ����� �ڿ�! ���� �г븦 �ڱ��ϴ� ����.!";
				Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
			}else if (getNpcTemplate().get_npcId() == 4039022) { // ���� ��Ÿ�� 3��
				String chat = "��Ÿ�� : ���� ���� ����Ϸ� �ϴٴ�..�׷��� ���� ��� �ٶ����?";
				Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
			} else if (getNpcTemplate().get_npcId() == 4039000) { // ���Ǿ� ��ȣ
				String chat = "��Ǫ���� : ���� ���� ������ �����ٴ�...��Ⱑ �����ϱ���..";
				Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
			}else if (getNpcTemplate().get_npcId() == 4039006) { // ���Ǿ� ��ȣ
				String chat = "��Ǫ���� : ������ Ǯ �� �װ� ū ������ �Ǿ�����..������ �� ���� �ں�� ����..";
				Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
			}else if (getNpcTemplate().get_npcId() == 4039007) { // ���Ǿ� ��ȣ
				String chat = "��Ǫ���� : ���ҷӱ���! ������ �ʿ� �Բ� �̽��� ������ �� ���� �������� ���̳�!";
				Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
			} else if (getNpcTemplate().get_npcId() == 9170) {//{���巹�̵�1
				String chat = "�������� : ���ҷӱ���! ������� ������� ���� ���� ��ȸ�ϰ� ������ְڴ�!";
				Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
			}else if (getNpcTemplate().get_npcId() == 9171) { //���巹�̵�2
				String chat = "�������� : �������� ���� Ǯ�� ���ߴµ�, ��ƺ����� ����!!";
				Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
			}else if (getNpcTemplate().get_npcId() == 9172) { //���巹�̵�3
				String chat = "�������� : ������ �� ������ ��� ����� ���� ������ ���� �����ֵ��� �ϰڴ�.";
				Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
			}else if (getNpcTemplate().get_npcId() == 9187) { //���巹�̵�4
				String chat = "������������ ���ҿ� ħ���� �� �����ΰ�?";
				Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
						chat, 0));
			}
                if (getNpcTemplate().get_npcId() == 45956) { // ���4�� ���� ��Ʈ
				for (L1PcInstance pc : L1World.getInstance()
						.getAllPlayers()) {
					if (pc.getMapId() == 531) {
						pc.sendPackets(new S_SystemMessage(
								"���Ÿ�� : ���� �̰��� ����� �˰� �� ������ ���� ���̴���!"));
					}
				}
				Thread.sleep(100L);
				for (L1PcInstance pc : L1World.getInstance()
						.getAllPlayers()) {
					if (pc.getMapId() == 531) {
						pc.sendPackets(new S_SystemMessage(
								"���Ÿ�� : ���������� ��ȸ�� ���״� �� ������ ��� ���� ���� �������."));
					}
				}
				if (getNpcTemplate().get_npcId() == 45957) {
					for (L1PcInstance pc : L1World.getInstance()
							.getAllPlayers()) {
						if (pc.getMapId() == 531) {
							pc.sendPackets(new S_SystemMessage(
									"�ٷθ޽� : ���� �Ƚ��� �����ϴ� �� ���� �ڰ� ��������?"));
						}
					}
					Thread.sleep(100L);
					for (L1PcInstance pc : L1World.getInstance()
							.getAllPlayers()) {
						if (pc.getMapId() == 531) {
							pc.sendPackets(new S_SystemMessage(
									"�ٷθ޽� : ������ ���ϰŵ� �����Է� ���ʶ�."));
						}
					}
					if (getNpcTemplate().get_npcId() == 45958) {
						for (L1PcInstance pc : L1World.getInstance()
								.getAllPlayers()) {
							if (pc.getMapId() == 531) {
								pc.sendPackets(new S_SystemMessage(
										"����ƽ� : �׶�ī���̽ÿ�! �� ����� �ڵ鿡�� ������ ����� ���� �����ּҼ�!"));
							}
						}
					}
				}
			}
			} catch (Exception exception) {
			}
			if (getNpcTemplate().is_doppel() && _target instanceof L1PcInstance) {
				L1PcInstance targetPc = (L1PcInstance) _target;
				setName(_target.getName());
				setNameId(_target.getName());
				setTitle(_target.getTitle());
				setTempLawful(_target.getLawful());
				getGfxId().setTempCharGfx(targetPc.getClassId());
				getGfxId().setGfxId(targetPc.getClassId());
				setPassispeed(640);
				setAtkspeed(900);
				FastTable<L1PcInstance> list = null;
				list = L1World.getInstance().getRecognizePlayer(this);
				for (L1PcInstance pc : list) {
					if (pc == null)
						continue;
					pc.sendPackets(new S_RemoveObject(this));
					pc.getNearObjects().removeKnownObject(this);
					pc.updateObject();
			}
			}
		}
		if (getCurrentHp() * 100 / getMaxHp() < 40) {
			useItem(USEITEM_HEAL, 50);
		}
	}

	@Override
	public void onPerceive(L1PcInstance perceivedFrom) {
		perceivedFrom.getNearObjects().addKnownObject(this);
		if (0 < getCurrentHp()) {
			if (getHiddenStatus() == HIDDEN_STATUS_SINK) {
				perceivedFrom.sendPackets(new S_DoActionGFX(getId(),
						ActionCodes.ACTION_Hide));
			} else if (getHiddenStatus() == HIDDEN_STATUS_FLY) {
				perceivedFrom.sendPackets(new S_DoActionGFX(getId(),
						ActionCodes.ACTION_Moveup));
			}
			onNpcAI();
		}
		perceivedFrom.sendPackets(new S_NPCPack(this));
		if (getNpcTemplate().get_npcId() == 4039020 && !seeon){
			Broadcaster.broadcastPacket(this, new S_DoActionGFX(getId(), ActionCodes.ACTION_Hide));
			seeon = true;
		}
		else if (getNpcTemplate().get_npcId() == 4039000 && !seeon){
		      Broadcaster.broadcastPacket(this, new S_DoActionGFX(getId(), ActionCodes.ACTION_Hide));
		   seeon = true;      
		}
	    else if (getNpcTemplate().get_npcId() == 9170 && !seeon){//���巹�̵�
	      Broadcaster.broadcastPacket(this, new S_DoActionGFX(getId(), ActionCodes.ACTION_Hide));
	   seeon = true;      
	}
	    else if (getNpcTemplate().get_npcId() == 9187 && !seeon){//���巹�̵�
		      Broadcaster.broadcastPacket(this, new S_DoActionGFX(getId(), ActionCodes.ACTION_Hide));
		   seeon = true;      
		}
	}

	@Override
	public void searchTarget() {
		L1PcInstance targetPlayer = null;
		/**����ƺ��б�**/
		L1ScarecrowInstance targetScarecrow = null;
		/**����ƺ��б�**/
		FastTable<L1PcInstance> list = null;
		list = L1World.getInstance().getVisiblePlayer(this);
		for (L1PcInstance pc : list) {
			if (pc == null)
				continue;
			if (pc.getCurrentHp() <= 0 || pc.isDead() || pc.isGm()
					|| pc.isMonitor() || pc.isGhost()) {
				continue;
			}

			int mapId = getMapId();
			if (mapId == 88 || mapId == 98 || mapId == 92 || mapId == 91
					|| mapId == 95) {
				if (!pc.isInvisble() || getNpcTemplate().is_agrocoi()) {
					targetPlayer = pc;
					break;
				}
			}

			if (getNpcId() == 45600) {
				if (pc.isCrown() || pc.isDarkelf()
						|| pc.getGfxId().getTempCharGfx() != pc.getClassId()) {
					targetPlayer = pc;
					break;
				}
			}
			/**ī���������**/
			if (getNpcId() == 45215) {
				if (pc.getLawful() <= -1) {
					targetPlayer = pc;
					break;
				}
			}

			if ((getNpcTemplate().getKarma() < 0 && pc.getKarmaLevel() >= 1)
					|| (getNpcTemplate().getKarma() > 0 && pc.getKarmaLevel() <= -1)) {
				continue;
			}

			// ���� ����Ʈ�� ����, �� ������ monster�κ��� ���� ���ݹ��� �ʴ´�
			if (pc.getGfxId().getTempCharGfx() == 6034
					&& getNpcTemplate().getKarma() < 0
					|| pc.getGfxId().getTempCharGfx() == 6035
					&& getNpcTemplate().getKarma() > 0
					|| pc.getGfxId().getTempCharGfx() == 6035
					&& getNpcTemplate().get_npcId() == 46070
					|| pc.getGfxId().getTempCharGfx() == 6035
					&& getNpcTemplate().get_npcId() == 46072) {
				continue;
			}

			if (!getNpcTemplate().is_agro() && !getNpcTemplate().is_agrososc()
					&& getNpcTemplate().is_agrogfxid1() < 0
					&& getNpcTemplate().is_agrogfxid2() < 0) {
				if (pc.getLawful() < -1000) { // -1000
					targetPlayer = pc;
					break;
				}
				continue;
			}

			if (!pc.isInvisble() || getNpcTemplate().is_agrocoi()) {
				if (pc.getSkillEffectTimerSet().hasSkillEffect(67)) {
					if (getNpcTemplate().is_agrososc()) {
						targetPlayer = pc;
						break;
					}
				} else if (getNpcTemplate().is_agro()) {
					targetPlayer = pc;
					break;
				}

				if (getNpcTemplate().is_agrogfxid1() >= 0
						&& getNpcTemplate().is_agrogfxid1() <= 4) {
					if (_classGfxId[getNpcTemplate().is_agrogfxid1()][0] == pc
							.getGfxId().getTempCharGfx()
							|| _classGfxId[getNpcTemplate().is_agrogfxid1()][1] == pc
									.getGfxId().getTempCharGfx()) {
						targetPlayer = pc;
						break;
					}
				} else if (pc.getGfxId().getTempCharGfx() == getNpcTemplate()
						.is_agrogfxid1()) {
					targetPlayer = pc;
					break;
				}

				if (getNpcTemplate().is_agrogfxid2() >= 0
						&& getNpcTemplate().is_agrogfxid2() <= 4) {
					if (_classGfxId[getNpcTemplate().is_agrogfxid2()][0] == pc
							.getGfxId().getTempCharGfx()
							|| _classGfxId[getNpcTemplate().is_agrogfxid2()][1] == pc
									.getGfxId().getTempCharGfx()) {
						targetPlayer = pc;
						break;
					}
				} else if (pc.getGfxId().getTempCharGfx() == getNpcTemplate()
						.is_agrogfxid2()) {
					targetPlayer = pc;
					break;
				}
			}
		} //������ ����ƺ� �б�
		for (L1Object obj : L1World.getInstance().getVisibleObjects(this)) {
	        if (obj instanceof L1ScarecrowInstance) {
	        	L1ScarecrowInstance mon = (L1ScarecrowInstance) obj;
	         if(mon.getHiddenStatus() != 0 || mon.isDead()){
	         continue;
	         } 
	if(this.getNpcTemplate().get_npcId()==7000007|| getNpcTemplate().get_npcId()==7000008 
			|| getNpcTemplate().get_npcId()==7000009 || getNpcTemplate().get_npcId()==7000010 
			|| getNpcTemplate().get_npcId()==7000011 ){ //���� �ν��� ���� 
	    if(mon.getNpcTemplate().get_npcId() == 45002){
	    	targetScarecrow = mon;
	    break;
	    }
	}
		if(this.getNpcTemplate().get_npcId()==7000012 || getNpcTemplate().get_npcId()==7000013
				|| getNpcTemplate().get_npcId()==7000014 || getNpcTemplate().get_npcId()==7000015 
				|| getNpcTemplate().get_npcId()==7000016){ //���� �ν��� ���� 
				    if(mon.getNpcTemplate().get_npcId() == 45001){
				    	targetScarecrow = mon;
				    break;
	         }
	   } 
	}
	} //<<�߰�
		if (targetPlayer != null) {
			_hateList.add(targetPlayer, 0);
			_target = targetPlayer;
		}
		if(targetScarecrow != null){ 
		      _hateList.add(targetScarecrow, 0);
		      _target = targetScarecrow;
		      } //<<����ƺ� �б�
	}

	@Override
	public void setLink(L1Character cha) {
		if (cha != null) {
			if (_hateList.isEmpty()) {
				_hateList.add(cha, 0);
				checkTarget();
			}
		}
	}

	public L1MonsterInstance(L1Npc template) {
		super(template);
		_storeDroped = 1;
	}

	@Override
	public void onNpcAI() {
		if (isAiRunning()) {
			return;
		}
		if (_storeDroped == 1) {
			DropTable.getInstance().setDrop(this, getInventory());
			getInventory().shuffle();
			_storeDroped = 0;
		} else if (_storeDroped == 2) {
			DropTable.getInstance().setPainwandDrop(this, getInventory());
			getInventory().shuffle();
			_storeDroped = 0;
		}
		setActived(false);
		startAI();
	}

	@Override
	public void onTalkAction(L1PcInstance pc) {
		if (pc == null)
			return;
		int objid = getId();
		L1NpcTalkData talking = NPCTalkDataTable.getInstance().getTemplate(
				getNpcTemplate().get_npcId());
		String htmlid = null;
		String[] htmldata = null;

		// html ǥ�� ��Ŷ �۽�
		if (htmlid != null) { // htmlid�� �����ǰ� �ִ� ���
			if (htmldata != null) { // html ������ �ִ� ���� ǥ��
				pc.sendPackets(new S_NPCTalkReturn(objid, htmlid, htmldata));
			} else {
				pc.sendPackets(new S_NPCTalkReturn(objid, htmlid));
			}
		} else {
			if (pc.getLawful() < -1000) { // -1000�÷��̾ ī��ƽ
				pc.sendPackets(new S_NPCTalkReturn(talking, objid, 2));
			} else {
				pc.sendPackets(new S_NPCTalkReturn(talking, objid, 1));
			}
		}
	}

	@Override
	public void onAction(L1PcInstance pc) {
		if (pc == null)
			return;
		if (getCurrentHp() > 0 && !isDead()) {
			L1Attack attack = new L1Attack(pc, this);
			if (attack.calcHit()) {
				attack.calcDamage();
				attack.addPcPoisonAttack(pc, this);
			}
			attack.action();
			attack.commit();
		}
	}

	@Override
	public void ReceiveManaDamage(L1Character attacker, int mpDamage) {
		if (attacker == null)
			return;
		if (mpDamage > 0 && !isDead()) {
			setHate(attacker, mpDamage);

			onNpcAI();

			if (attacker instanceof L1PcInstance) {
				serchLink((L1PcInstance) attacker, getNpcTemplate()
						.get_family());
			}

			int newMp = getCurrentMp() - mpDamage;
			if (newMp < 0) {
				newMp = 0;
			}
			setCurrentMp(newMp);
		}
	}

	public int testss(L1Character pc) {
		if (pc.getSkillEffectTimerSet().hasSkillEffect(7836)) {
			return 1;
		} else
			return 2;
	}
	private int transRiperid(int mapid) {
	       int id = (mapid - 100) / 10;
	      int mobid = 0;
	      switch (id) {
	       case 0: 
	    	   mobid = 45513; 
	    	   Broadcaster.broadcastPacket(this, new S_SkillSound(getId(), 4784));
	    	   break; // 1���� - 10������
	       case 1: 
	    	   mobid = 45547; 
	    	   Broadcaster.broadcastPacket(this, new S_SkillSound(getId(), 4784));
	    	   break; // 10���� - 20������
	       case 2: 
	    	   mobid = 45606; 
	    	   Broadcaster.broadcastPacket(this, new S_SkillSound(getId(), 4784));
	    	   break; // 20���� - 30������
	      case 3: 
	    	  mobid = 45650; 
	    	  Broadcaster.broadcastPacket(this, new S_SkillSound(getId(), 4784));
	    	  break; // 30���� - 40������
	       case 4: 
	    	   mobid = 45652; 
	    	   Broadcaster.broadcastPacket(this, new S_SkillSound(getId(), 4784));
	    	   break; // 40���� - 50������
	       case 5: 
	    	   mobid = 45653; 
	    	   Broadcaster.broadcastPacket(this, new S_SkillSound(getId(), 4784));
	    	   break; // 50���� - 60������
	       case 6: 
	    	   mobid = 45654; 
	    	   Broadcaster.broadcastPacket(this, new S_SkillSound(getId(), 4784));
	    	   break; // 60���� - 70������
	      case 7: 
	    	  mobid = 45618; 
	    	  Broadcaster.broadcastPacket(this, new S_SkillSound(getId(), 4784));
	    	  break; // 70���� - 80������
	      case 8: 
	    	  mobid = 45672; 
	    	  Broadcaster.broadcastPacket(this, new S_SkillSound(getId(), 4784));
	    	  break; // 80���� - 90������
	       case 9: 
	    	   mobid = 45673; 
	    	   Broadcaster.broadcastPacket(this, new S_SkillSound(getId(), 4784));
	    	   break; // 90���� - 100������
	       }
	      return mobid;
	    }
	 
	   private void dead(L1Character attacker) {
	      setCurrentHp(0);
	       setDead(true);
	       setActionStatus(ActionCodes.ACTION_Die);
	      dead.setAttacker(attacker);
	      dead.run();  
	    }
	@Override
	public void receiveDamage(L1Character attacker, int damage) {
		if (attacker == null)
			return;
		if (getCurrentHp() > 0 && !isDead()) {

			if (getHiddenStatus() != HIDDEN_STATUS_NONE
					|| getHiddenStatus() == HIDDEN_STATUS_FLY) {
				return;
			}
			if (damage >= 0) {
				if (!(attacker instanceof L1EffectInstance)) { 
					setHate(attacker, damage);
				}
			}
			if (damage > 0) {
				if (getSkillEffectTimerSet().hasSkillEffect(
						L1SkillId.FOG_OF_SLEEPING)) {
					getSkillEffectTimerSet().removeSkillEffect(
							L1SkillId.FOG_OF_SLEEPING);
				} else if (getSkillEffectTimerSet().hasSkillEffect(
						L1SkillId.PHANTASM)) {
					getSkillEffectTimerSet().removeSkillEffect(
							L1SkillId.PHANTASM);
				}
			}

			onNpcAI();

			if (attacker instanceof L1PcInstance) {
				serchLink((L1PcInstance) attacker, getNpcTemplate()
						.get_family());
			}

			if (attacker instanceof L1PcInstance && damage > 0) {
				L1PcInstance player = (L1PcInstance) attacker;
				player.setPetTarget(this);
							//������������ȯ
				if (getNpcTemplate().get_npcId() == 45681
						|| getNpcTemplate().get_npcId() == 45601
						|| getNpcTemplate().get_npcId() == 45682
						|| getNpcTemplate().get_npcId() == 45683
						|| getNpcTemplate().get_npcId() == 500060
						|| getNpcTemplate().get_npcId() == 500061
						|| getNpcTemplate().get_npcId() == 81163
						|| getNpcTemplate().get_npcId() == 4036016
						|| getNpcTemplate().get_npcId() == 4036017
						|| getNpcTemplate().get_npcId() == 45684
						|| getNpcTemplate().get_npcId() == 45617
						|| getNpcTemplate().get_npcId() == 45516
						|| getNpcTemplate().get_npcId() == 45545
						|| getNpcTemplate().get_npcId() == 460000035 //��տ�¡��
						|| getNpcTemplate().get_npcId() == 460000036 //��տ�¡�� �ٸ�
						//|| getNpcTemplate().get_npcId() == 65498 //�����
						|| getNpcTemplate().get_npcId() == 65499 //����� �н�
						|| getNpcTemplate().get_npcId() == 45529) {
					recall(player);
				}
}
				if (getNpcTemplate().get_npcId() == 4039000) {
					sendPackets(new S_SkillSound(this.getId(), 761));
					Broadcaster.broadcastPacket(this, new S_SkillSound(this
							.getId(), 761));
				}
				if (getNpcTemplate().get_npcId() == 4039003) {
					damage = 0;
					return;

				}
			
			int newHp = getCurrentHp() - damage;
			if (newHp <= 0 && !isDead()) { 
				
		        if ((getNpcTemplate().get_npcId() == 5000115) && 
		                ((attacker instanceof L1PcInstance))) {
		                L1PcInstance pc = (L1PcInstance)attacker;
		                if ((pc != null) && (pc.getMapId() >= 807) && (pc.getMapId() <= 813)) {
		                  L1Location newLocation = pc.getLocation().randomLocation(200, true);
		                  int x = newLocation.getX();
		                  int y = newLocation.getY();
		                  short mapid = (short)newLocation.getMapId();
		                  int heading = pc.getMoveState().getHeading();
		                  L1Teleport.teleport(pc, x, y, mapid, heading, true);
		                }
		              }

		              if ((getNpcTemplate().get_npcId() == 5000116) && 
		                ((attacker instanceof L1PcInstance))) {
		                L1PcInstance pc = (L1PcInstance)attacker;
		                if ((pc != null) && (pc.getMapId() >= 807) && (pc.getMapId() <= 813)) {
		                  L1Location newLocation = pc.getLocation().randomLocation(200, true);
		                  int x = newLocation.getX();
		                  int y = newLocation.getY();
		                  short mapid = (short)newLocation.getMapId();
		                  int heading = pc.getMoveState().getHeading();
		                  if (pc.getMapId() == 807)
		                    L1Teleport.teleport(pc, x, y, mapid, heading, true);
		                  else {
		                    L1Teleport.teleport(pc, x, y, (short)(mapid - 1), heading, true);
		                  }
		                }
		              }
				
				Random random = new Random();    // �߰�
				int chance1 = random.nextInt(100) + 1;
				int chance2 = random.nextInt(100) + 1; 
				int rnd = 25 * random.nextInt(100); //��������Ʈ �߰�
					// ����Ʈ ��� �κ�üũ
				if (Config.RATE_SILVER > chance1) { //��������Ʈ ������ ���� ���
				     if (attacker.getInventory().checkItem(5437)) { //�����±��� ��� �ڵ� ( Db��ġ�� )
				      if (getNpcTemplate().get_npcId() >= 123478 //���͹�ȣ
					    	  || getNpcTemplate().get_npcId() == 123466	//����� �ʵ� ������
					    	  || getNpcTemplate().get_npcId() == 123467	//����� �ʵ� ������
				    	  	  || getNpcTemplate().get_npcId() == 123468	//����� �ʵ� ������
				    	  	  || getNpcTemplate().get_npcId() == 123469	//����� �ʵ� ������
						      || getNpcTemplate().get_npcId() == 123470//����� �ʵ� ������
							  || getNpcTemplate().get_npcId() == 123471//����� �ʵ� ������
							  || getNpcTemplate().get_npcId() == 123472	//����� �ʵ� ������
							  || getNpcTemplate().get_npcId() == 123473//����� �ʵ� ������
							  || getNpcTemplate().get_npcId() == 123474//����� �ʵ� ������
							  || getNpcTemplate().get_npcId() == 123475//����� �ʵ� ������
							  || getNpcTemplate().get_npcId() == 123476	//����� �ʵ� ������
							  || getNpcTemplate().get_npcId() == 123477//����� �ʵ� ������
							  || getNpcTemplate().get_npcId() == 123478//����� �ʵ� ������
				    	  	  || getNpcTemplate().get_npcId() == 123479)//����� �ʵ� ������
				       getInventory().storeItem(5439, 1); // �������ǥ
				     }
				    }
				    if (Config.RATE_SILVER > chance1) { //��������Ʈ ��������
					     if (attacker.getInventory().checkItem(5456)) { //�����ѱ��� ��ȣ
						      if (getNpcTemplate().get_npcId() >= 980076 //���� ���õ巡��
						    	  || getNpcTemplate().get_npcId() == 980072	//���� ���ϵ巡��
						    	  || getNpcTemplate().get_npcId() == 980073	//���� ���ϵ巡��
						    	  || getNpcTemplate().get_npcId() == 980074	//���� ���ϵ巡��
					    	  	  || getNpcTemplate().get_npcId() == 980075	//���� ���ϵ巡��
					    	  	  || getNpcTemplate().get_npcId() == 980076	//���� ���ϵ巡��
							      || getNpcTemplate().get_npcId() == 4500124//���� ���ϵ巡��
								  || getNpcTemplate().get_npcId() == 4500125//���� ���ϵ巡��
					    	  	  || getNpcTemplate().get_npcId() == 4200123)//�巡�� ��ȣ��
						       getInventory().storeItem(5440, 1); // �����巡���ǻ� 
						     }
						    }
				    if (Config.RATE_SILVER > chance2) { //���� ���� ����������
				     if (attacker.getsub() == 1) {
				      if (getNpcTemplate().get_npcId() >= 123550 //���� ���� ������
					    	  || getNpcTemplate().get_npcId() == 123551	//����� �ʵ� ������
					    	  || getNpcTemplate().get_npcId() == 123552	//����� �ʵ� ������
				    	  	  || getNpcTemplate().get_npcId() == 123553	//����� �ʵ� ������
				    	  	  || getNpcTemplate().get_npcId() == 123450	//����� �ʵ� ������
						      || getNpcTemplate().get_npcId() == 123451//����� �ʵ� ������
							  || getNpcTemplate().get_npcId() == 123452//����� �ʵ� ������
							  || getNpcTemplate().get_npcId() == 123453	//����� �ʵ� ������
							  || getNpcTemplate().get_npcId() == 123454//����� �ʵ� ������
							  || getNpcTemplate().get_npcId() == 123455//����� �ʵ� ������
							  || getNpcTemplate().get_npcId() == 123456//����� �ʵ� ������
							  || getNpcTemplate().get_npcId() == 123457	//����� �ʵ� ������
							  || getNpcTemplate().get_npcId() == 123458//����� �ʵ� ������
							  || getNpcTemplate().get_npcId() == 123459//����� �ʵ� ������
							  || getNpcTemplate().get_npcId() == 123460//����� �ʵ� ������
							  || getNpcTemplate().get_npcId() == 123461//����� �ʵ� ������
				    	  	  || getNpcTemplate().get_npcId() == 123462)//����� �ʵ� ������
				       getInventory().storeItem(5440, 1); // ���͹���
				     }
				    }
				    if (Config.RATE_SILVER > chance2) {
				     if (attacker.getsub() == 2) {
				    	 if (getNpcTemplate().get_npcId() >= 123550 //���� ���� ������
						    	  || getNpcTemplate().get_npcId() == 123551	//���� ���� ������
						    	  || getNpcTemplate().get_npcId() == 123552	//���� ���� ������
					    	  	  || getNpcTemplate().get_npcId() == 123553	//���� ���� ������
					    	  	  || getNpcTemplate().get_npcId() == 123450	//���� ���� ������
							      || getNpcTemplate().get_npcId() == 123451//���� ���� ������
								  || getNpcTemplate().get_npcId() == 123452//���� ���� ������
								  || getNpcTemplate().get_npcId() == 123453	//���� ���� ������
								  || getNpcTemplate().get_npcId() == 123454//���� ���� ������
								  || getNpcTemplate().get_npcId() == 123455//���� ���� ������
								  || getNpcTemplate().get_npcId() == 123456//���� ���� ������
								  || getNpcTemplate().get_npcId() == 123457	//���� ���� ������
								  || getNpcTemplate().get_npcId() == 123458//���� ���� ������
								  || getNpcTemplate().get_npcId() == 123459//���� ���� ������
								  || getNpcTemplate().get_npcId() == 123460//���� ���� ������
								  || getNpcTemplate().get_npcId() == 123461//���� ���� ������
					    	  	  || getNpcTemplate().get_npcId() == 123462)//���� ���� ������
				       getInventory().storeItem(5441, 1); // �����̻�
				     }
				    }
				    if (Config.RATE_SILVER > chance2) {
				     if (attacker.getsub() == 3) {
				      if (getNpcTemplate().get_npcId() >= 45119 //1��
					    	  || getNpcTemplate().get_npcId() == 123465	//���� ���� ������
					    	  || getNpcTemplate().get_npcId() == 123466	//���� ���� ������
				    	  	  || getNpcTemplate().get_npcId() == 123467	//���� ���� ������
				    	  	  || getNpcTemplate().get_npcId() == 123468	//���� ���� ������
						      || getNpcTemplate().get_npcId() == 123469//���� ���� ������
							  || getNpcTemplate().get_npcId() == 123470//���� ���� ������
				    	  	  || getNpcTemplate().get_npcId() == 123471)//���� ���� ������
				       getInventory().storeItem(5442, 1); // �콼����
				     }
				    }
				    if (Config.RATE_SILVER > chance2) {
				     if (attacker.getsub() == 4) {
				      if (getNpcTemplate().get_npcId() >= 45119 //2��
					    	  || getNpcTemplate().get_npcId() == 123468	//���� ���� ������
					    	  || getNpcTemplate().get_npcId() == 123470	//���� ���� ������
				    	  	  || getNpcTemplate().get_npcId() == 123472	//���� ���� ������
				    	  	  || getNpcTemplate().get_npcId() == 123473	//���� ���� ������
						      || getNpcTemplate().get_npcId() == 123474//���� ���� ������
							  || getNpcTemplate().get_npcId() == 123471//���� ���� ������
				    	  	  || getNpcTemplate().get_npcId() == 123475)//���� ���� ������
				       getInventory().storeItem(5443, 1); // �콼�尩
				     }
				    }
				    if (Config.RATE_SILVER > chance2) {
				     if (attacker.getsub() == 6) {
				      if (getNpcTemplate().get_npcId() >= 45082 //���ô��� 3~4�����͹�ȣ��
					    	  || getNpcTemplate().get_npcId() == 123475	//���� ���� ������
					    	  || getNpcTemplate().get_npcId() == 123477	//���� ���� ������
				    	  	  || getNpcTemplate().get_npcId() == 123478	//���� ���� ������
				    	  	  || getNpcTemplate().get_npcId() == 123479)//���� ���� ������
				       getInventory().storeItem(5444, 1); // �콼��ȭ
				     }
				    }
			
				if(Config.RATE_DREAM > chance1){ 
					getInventory().storeItem(438015, 1); // ���������� �������
				}
				if(Config.RATE_DREAM > chance2){ 
					getInventory().storeItem(438015, 1); // ���� ��������
				}
				
				int npcid = getNpcTemplate().get_npcId();
					if (npcid == 45956) {
					for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
						if (pc.getMapId() == 531) {
						pc.sendPackets(new S_SystemMessage("���Ÿ�� : ��Ÿ�ٵ��� �����̿�, ���� �϶�!"));
						}
					}
				}
				if (npcid == 45957) {
					for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
						if (pc.getMapId() == 531) {
						pc.sendPackets(new S_SystemMessage("�ٷθ޽� : �̴�� ������ ��Ÿ�ٵ尡 �ƴϴ�. �׾�� ����� ��������."));
						}
					}
				}
				if (npcid == 45958) {
					for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
						if (pc.getMapId() == 531) {
						pc.sendPackets(new S_SystemMessage("����ƽ� : ���̽ÿ�! �����Ͽ� ���� �����ʴϱ�?"));
						}
					}
				}
				if (npcid == 46141 || npcid == 46142) { //����������
					if (attacker instanceof L1PcInstance) { 
						GeneralThreadPool.getInstance().schedule(new KillBossBuffTimer(this), 10 * 1000);
					}
				}
				if (npcid == 4036016 || npcid == 4036017// ���극�� ������
						|| npcid == 400016 || npcid == 400017||//�׺�
						npcid == 4039020||npcid == 4039000 ||npcid == 4039021||npcid == 4039006
						|| npcid == 9170 || npcid == 9171) { //1~2���븰�巹�̵�
					if (attacker instanceof L1PcInstance) { 
						GeneralThreadPool.getInstance().schedule(new KillBossBuffTimer(this), 10 * 1000);
					}
				}
				 if (npcid == 4039000) { // ���Ǿ� ��ȣ
						String chat = "��Ǫ���� : ���հ����δ� ����ϱ���! ������...";
						Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
				} 
				 if (npcid == 4039006) { // ���Ǿ� ��ȣ
						String chat = "��Ǫ���� : �� �ӱ��� �İ��� �η����� �������� �� ���� �˰� ���ָ�!";
						Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
				}
				if (npcid == 4039007) { // ���Ǿ� ��ȣ
						String chat = "��Ǫ���� : �翤..�� �༮��..���...���� ��Ӵ�..�Ƿ��̽ÿ� ���� ����..�ŵμҼ�...";
						Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
						L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("���̵� ��Ǫ������ ���� ���߽��ϴ�."));
						 dragonportalspawn(4212013,33726,32506,(short)4,6,720);
						 L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("�������� ��ħ : ���� ������ ������ ����� ������ ���� ���� ���Ƚ��ϴ�."));
						if (attacker instanceof L1PcInstance) {
							GeneralThreadPool.getInstance().schedule(new KillBossBuffTimer(this), 10 * 1000);
					}
					}
				 if (npcid == 4039020) { // 
						String chat = "��Ÿ�� : ���� ���ִ� �Ļ縦 �غ���? ���� �ǳ����� ���� ��ġ�� �ϴ±���.";
						Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
				}
				 if (npcid == 4039021) { // ���Ǿ� ��ȣ 
						String chat = "��Ÿ�� : ���� �г밡 �ϴÿ� ��Ҵ�. ���� �� ���� �ƹ����� ���� ���̴�.";
						Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
				}
				 if (npcid == 4039022) { // ���Ǿ� ��ȣ 
						String chat = "��Ÿ�� : Ȳȥ�� ���ְ� �״�鿡�� ���� �����! �Ƿ��̿�. ���� ��ӴϿ�. ���� ����.. �ŵμҼ�...";
						Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
						L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("���̵� ��Ÿ�󽺰� ���� ���߽��ϴ�."));
						 dragonportalspawn(4212013,33726,32506,(short)4,6,720);
					    L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("�������� ��ħ : ���� ������ ������ ����� ������ ���� ���� ���Ƚ��ϴ�."));
						if (attacker instanceof L1PcInstance) {
							GeneralThreadPool.getInstance().schedule(
									new KillBossBuffTimer(this), 10 * 1000);
					}
					}
				 if (npcid == 9170) { //���巹�̵�1 
						String chat = "�������� : �׷��� �����̱���! ������ �������� ��ƿ �� ������?";
						Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
			    }
				 if (npcid == 9171) { //���巹�̵�2 
						String chat = "�������� : ���� �躸�Ҵ� �Ͱ���. �̹��� ��� �ñ��ϱ�.";
						Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
				}
				 if (npcid == 9172) { //���巹�̵�3 
						String chat = "�������� : �ƾ�~!! ���� ��Ӵ� �Ƿ��̿� ���� ����� �ּҼ�....";
						Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
						L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("���̵� ���������� ���� ���߽��ϴ�."));
						 dragonportalspawn(4212013,33726,32506,(short)4,6,720);
						 L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("�������� ��ħ : ���� ������ ������ ����� ������ ���� ���� ���Ƚ��ϴ�."));
						if (attacker instanceof L1PcInstance) {
							GeneralThreadPool.getInstance().schedule(
									new KillBossBuffTimer(this), 10 * 1000);
					}
					}
					if (npcid == 9187) { //���巹�̵�
						String chat = "���..���� ������������ �ɱ⸦ �����ϰ� �ϴٴ�!";
						Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
								chat, 0));
					}
				if (npcid == 45590) { // ���Ǿ� ��ȣ
					String chat = "ũ��..�ΰ��� ġ��� �����̱���..";
					Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
							chat, 0));
				}else if (npcid == 45513) { //  
					String chat = "���ƾ�! �̷� ���� �ȵǴ� ����!";
					Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
							chat, 0));
				}else if (npcid == 45547) { // ���Ǿ� ��ȣ
					String chat = "������Ÿ��... ���..��� ��Ű���...";
					Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
							chat, 0));
				}else if (npcid == 45606) { // ���Ǿ� ��ȣ
					String chat = "�������!...";
					Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
							chat, 0));
				}else if (npcid == 45650) { // ���Ǿ� ��ȣ
					String chat = "��..�̷�����..�� ������ ���� �̰ܳ��ٴ�..";
					Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
							chat, 0));
				}else if (npcid == 45652) { // ���Ǿ� ��ȣ
					String chat = "��..��...�����ո�..�־��..��..���ϴ�..";
					Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
							chat, 0));
				}else if (npcid == 45653) { // ���Ǿ� ��ȣ
					String chat = "ũ����������...";
					Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
							chat, 0));
				}else if (npcid == 45654) { // ���Ǿ� ��ȣ
					String chat = "��..�ƹ���...��� ��ʴϱ�..?";
					Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
							chat, 0));
				}else if (npcid == 45618) { // ���Ǿ� ��ȣ
					String chat = "��..���� ������ ���..�Ǿ��°�?..";
					Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
							chat, 0));
				}else if (npcid == 45672) { // ���Ǿ� ��ȣ
					String chat = "���� ����Ʈ�� ���� �״���ΰ�? ���� ���� ���شٿ�..��Ź����..";
					Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
							chat, 0));
				}else if (npcid == 45673) { // ���Ǿ� ��ȣ
					String chat = "�ߵ�..�ߵ� �״� �����Ѱ�? ���Ͻ�..�׳��?";
					Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
							chat, 0));
				}else if (npcid == 46141) { //���
					String chat = "Ȥ���� �ٶ��̿� �̵��� �������� ���ٰ� �϶�!";
					Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
							chat, 0));
				}else if (npcid == 46142) { //����
					String chat = "���� Į�����̿� ������ ��� ���������!";
					Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
							chat, 0));
				}
				int transformId = getNpcTemplate().getTransformId();
				int mapid = getMapId();
				 if ((getMapId() >= 101 && getMapId() <= 200)
				      && mapid % 10 != 0) {
				   int rnd_Reper = _random.nextInt(1000);//rnd_Reper
				   if (rnd < 20) { // 0.5%
				      int rid = transRiperid(mapid);
				      if (getNpcTemplate().get_npcId() != 45590 && getNpcTemplate().get_npcId() != rid)
				         transform(45590); // ������ ����
				      else if (getNpcTemplate().get_npcId() == 45590) { // ������ ����
				        transform(rid);
				      }
				    } else   dead(attacker);
				} else if (transformId == -1) {
					setCurrentHp(0);
					setDead(true);
					setActionStatus(ActionCodes.ACTION_Die);

					dead.setAttacker(attacker);
				dead.run();
					if(ismarble()){
					
			    		setCurrentHp(0);
						setDead(true);
						attacker.marble.remove("��������");
						hprsize = attacker.marble.size();
						setActionStatus(ActionCodes.ACTION_Die);
						dead.setAttacker(attacker);
						dead.run();
						if(attacker.marble.size() == 0 && attacker.marble2.size()==0){
						}
					}
					if (ismarble2()) {
						setCurrentHp(0);
						setDead(true);
						attacker.marble2.remove("�ź��ѿ�������");

						if (attacker.marble2.size() < 1) {
							sendPackets(new S_SkillHaste(this.getId(), 0, 0));
							Broadcaster.broadcastPacket(this, new S_SkillHaste(
									this.getId(), 0, 0));
							getMoveState().setMoveSpeed(0);
						}
						setActionStatus(ActionCodes.ACTION_Die);
						dead.setAttacker(attacker);
						dead.run();
					}
				} else {

					if (isAntharas()) {
						setCurrentHp(0);
						setDead(true);
						dieAntharas(attacker);
					}else if (isPapoo()) {
						setCurrentHp(0);
						setDead(true);
						diePaPoo(attacker);
					}else if (isPind()) {
						setCurrentHp(0);
						setDead(true);
						diePind(attacker);
					}

					else {
						transform(transformId);
					}
				}
			}
			if (newHp > 0) {
				setCurrentHp(newHp);
				hide();
			}
		} else if (!isDead()) {
			setDead(true);
			setActionStatus(ActionCodes.ACTION_Die);
			dead.setAttacker(attacker);
			dead.run();
		}
	}
	public void setDeath(Dead d) {
		dead = d;
	}

	private void recall(L1PcInstance pc) {
		if (pc == null || getMapId() != pc.getMapId()) {
			return;
		}
		if (getLocation().getTileLineDistance(pc.getLocation()) > 4) {
			L1Location newLoc = null;
			for (int count = 0; count < 10; count++) {
				newLoc = getLocation().randomLocation(3, 4, false);
				if (CharPosUtil.glanceCheck(this, newLoc.getX(), newLoc.getY())) {
					L1Teleport.teleport(pc, newLoc.getX(), newLoc.getY(),
							getMapId(), 5, true);
					break;
				}
			}
		}
	}

	@Override
	public void setCurrentHp(int i) {
		super.setCurrentHp(i);

		if (getMaxHp() > getCurrentHp()) {
			startHpRegeneration();
		}
	}

	@Override
	public void setCurrentMp(int i) {
		super.setCurrentMp(i);

		if (getMaxMp() > getCurrentMp()) {
			startMpRegeneration();
		}
	}

	public void die(L1Character lastAttacker) {
		try {
			setDeathProcessing(true);
			setCurrentHp(0);
			setDead(true);			
			setActionStatus(ActionCodes.ACTION_Die);
			getMap().setPassable(getLocation(), true);
			Broadcaster.broadcastPacket(this, new S_DoActionGFX(getId(),ActionCodes.ACTION_Die));
			startChat(CHAT_TIMING_DEAD);
			distributeExpDropKarma(lastAttacker);			
			giveUbSeal();
			setDeathProcessing(false);
			setExp(0);
			setKarma(0);
			setLawful(0);
			allTargetClear();
			startDeleteTimer();
		} catch (Exception e) {
		}
	}
	public void die3() {
		try {
			setDeathProcessing(true);
			setCurrentHp(0);
			setDead(true);
			getMap().setPassable(getLocation(), true);
			startChat(CHAT_TIMING_DEAD);
			setDeathProcessing(false);
			setExp(0);
			setKarma(0);
			setLawful(0);
			allTargetClear();
			deleteMe();
		} catch (Exception e) {
		}
	}

	private void die2(L1Character lastAttacker) {
		try {
			setDeathProcessing(true);
			setCurrentHp(0);
			setDead(true);
			getMap().setPassable(getLocation(), true);
			startChat(CHAT_TIMING_DEAD);
			setDeathProcessing(false);
			setExp(0);
			setKarma(0);
			setLawful(0);
			allTargetClear();
			int transformGfxId = getNpcTemplate().getTransformGfxId();
			if (transformGfxId > 0)
				Broadcaster.broadcastPacket(this, new S_SkillSound(getId(),
						transformGfxId));
			deleteMe();
			GeneralThreadPool.getInstance().schedule(new GiranTransTimer(this),
					400);
		} catch (Exception e) {
		}
	}
	private void dieAntharas(L1Character lastAttacker) {
		setDeathProcessing(true);
		setCurrentHp(0);
		setDead(true);
		getMap().setPassable(getLocation(), true);
		startChat(CHAT_TIMING_DEAD);
		setDeathProcessing(false);
		setExp(0);
		setKarma(0);
		allTargetClear();
		int transformGfxId = getNpcTemplate().getTransformGfxId();
		if (transformGfxId > 0)
			Broadcaster.broadcastPacket(this, new S_SkillSound(getId(),
					transformGfxId));
		setActionStatus(ActionCodes.ACTION_Die);
		Broadcaster.broadcastPacket(this, new S_DoActionGFX(getId(),
				ActionCodes.ACTION_Die));
		deleteMe();
		int npcid = getNpcTemplate().get_npcId();
		switch (npcid) {
		case 4039020:
			GeneralThreadPool.getInstance().schedule(
					new AntharasTransTimer(this), 20 * 1000);
			break;
		case 4039021:
			GeneralThreadPool.getInstance().schedule(
					new AntharasTransTimer2(this), 20 * 1000);
			break;
		}
	}
	private static class AntharasTransTimer implements Runnable{//extends TimerTask {
		L1NpcInstance _npc;

		private AntharasTransTimer(L1NpcInstance some) {
			_npc = some;
		}

		public void run() {
			L1SpawnUtil.spawn2(_npc.getX(), _npc.getY(), (short) _npc.getMap().getId(), 4039021, 0, 0, 0);
		}
	}
	private static class AntharasTransTimer2 implements Runnable{
		L1NpcInstance _npc;

		private AntharasTransTimer2(L1NpcInstance some) {
			_npc = some;
		}

		public void run() {
			L1SpawnUtil.spawn2(_npc.getX(), _npc.getY(), (short) _npc.getMap().getId(), 4039022, 0, 0, 0);
		}
	}
	private static class KillBossBuffTimer implements Runnable{
		int _bosstype = 0;
		L1SkillUse l1skilluse = new L1SkillUse();
		L1MonsterInstance mon = null;

		private KillBossBuffTimer(L1MonsterInstance npc) {
			mon = npc;
			_bosstype = npc.getNpcTemplate().get_npcId();
		}

		public void run() {
			try {
				if (_bosstype != 0) {
					if (_bosstype == 4039022) { // ��Ÿ���ǰ��
						for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
							if(pc == null)
								continue;
							pc.setBuffnoch(1);
			                if(pc.getMapId() == 1005){//�̸�������
			                	pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
			                	l1skilluse.handleCommands(pc,L1SkillId.DRAGONBLOOD_ANTA, pc.getId(), pc.getX(), pc.getY(), null, 0,L1SkillUse.TYPE_GMBUFF);//�����ְ�
			                	pc.sendPackets(new S_ServerMessage(1628));//�޼���
			                	pc.setBuffnoch(0);
			                }
						}
						Mapdrop(mon);
						for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			                if(pc.getMapId() == 1005){//�̸�������
			                	pc.getInventory().storeItem(408986, 1);//ǥ���ֱ�
			                	pc.sendPackets(new S_SystemMessage("������ ǥ���� ���� �Ǿ����ϴ�."));//�޼���
			                //	pc.sendPackets(new S_ServerMessage(1477));
			                	S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"20�� �� ������ �ڵ� ��ȯ �˴ϴ�.", Opcodes.S_OPCODE_MSG, 20); 
			                	pc.sendPackets(s_chatpacket);
			                	pc.sendPackets(new S_ServerMessage(1581));
			                }
						}
	                	Thread.sleep(20000);//20���ĸ�����
						for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			                if(pc!=null && pc.getMapId() == 1005){//�̸�������
			                	L1Teleport.teleport(pc, 33700, 32505, (short) 4, 5, true); // ���� ����
			                }
						}
					}
					if (_bosstype == 4039007) { // ��Ǫ���
						for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
							if(pc == null)
								continue;
							pc.setBuffnoch(1);
							if(pc.getMapId() == 1011){//�̸�������
								pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
								l1skilluse.handleCommands(pc, L1SkillId.DRAGONBLOOD_PAP, pc.getId(), pc.getX(), pc.getY(), null, 0,L1SkillUse.TYPE_GMBUFF);
								pc.sendPackets(new S_ServerMessage(1644));
								pc.setBuffnoch(0);
							}
						}
						Mapdrop(mon);
						for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			                if(pc.getMapId() == 1011){//�̸�������
								pc.getInventory().storeItem(408987, 1);//ǥ���ֱ�
								pc.sendPackets(new S_SystemMessage("������ ǥ���� ���� �Ǿ����ϴ�."));//�޼���
							//	pc.sendPackets(new S_ServerMessage(1477));
			                	S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"20�� �� ������ �ڵ� ��ȯ �˴ϴ�.", Opcodes.S_OPCODE_MSG, 20); 
			                	pc.sendPackets(s_chatpacket);
								pc.sendPackets(new S_ServerMessage(1669));
			                }
			                }
	                	Thread.sleep(20000);//20���ĸ�����
						for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			                if(pc != null && pc.getMapId() == 1011){//�̸�������
			                	L1Teleport.teleport(pc, 33700, 32505, (short) 4, 5, true); // ���� ����
			                }
						}
					}
					if (_bosstype == 9172) { // ���巹�̵�
						for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
							if(pc == null)
								continue;
							pc.setBuffnoch(1);
			                if(pc.getMapId() == 1017){//�̸�������
			                	pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
			                	l1skilluse.handleCommands(pc,L1SkillId.DRAGONBLOOD_RIND, pc.getId(), pc.getX(), pc.getY(), null, 0,L1SkillUse.TYPE_GMBUFF);//�����ְ�
			                	pc.sendPackets(new S_SystemMessage("���������� ���翡 ���� ������ ������ ��ϴ�."));//�޼���
			                	pc.setBuffnoch(0);
			                }
						}
						Mapdrop(mon);
						for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			                if(pc.getMapId() == 1017){//�̸�������
			                	pc.getInventory().storeItem(408989, 1);//ǥ���ֱ�
			                	pc.sendPackets(new S_SystemMessage("ǳ���� ǥ���� ���� �Ǿ����ϴ�."));//�޼���
			                	//pc.sendPackets(new S_ServerMessage(1477));
			                	S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"20�� �� ������ �ڵ� ��ȯ �˴ϴ�.", Opcodes.S_OPCODE_MSG, 20); 
			                	pc.sendPackets(s_chatpacket);
			                	pc.sendPackets(new S_SystemMessage("�������� ��ħ : ���������� ������ ���� ������ ź�� �Ͽ����ϴ�.!!"));
			                }
			                }
	                	Thread.sleep(20000);//20���ĸ�����
						for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			                if(pc!=null && pc.getMapId() == 1017){//�̸�������
			                	L1Teleport.teleport(pc, 33700, 32505, (short) 4, 5, true); // ���� ����
			                }
						}
					}
					if (_bosstype == 46142 || _bosstype == 46141) { // ����
						for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
							if(pc == null)
								continue;
							if(pc.getMapId() == 2101 || pc.getMapId() == 2151){//�̸�������
								pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"������ ���͸� ���������� �� �ڿ� �ִ� ������ �����ʽÿ�."));//�޼���
							}
						}
					}
					if (_bosstype == 4036016) { // ��귡�� ��,��
						for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
							if(pc == null)
								continue;
							pc.setBuffnoch(1);
							if(pc.getMapId() == 784){//�̸�������
							l1skilluse.handleCommands(pc,
									L1SkillId.STATUS_TIKAL_BOSSDIE, pc.getId(),
									pc.getX(), pc.getY(), null, 0,
									L1SkillUse.TYPE_GMBUFF);
							pc.sendPackets(new S_SystemMessage("���극���� �������� ���ž�� �ູ�� 60�� �����˴ϴ�."));
							pc.setBuffnoch(0);
						}
						}
						Mapdrop(mon);
					}
					if (_bosstype == 4036017) { // ��귡�� ��,��
						for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
							if(pc == null)
								continue;
							pc.setBuffnoch(1);
							if(pc.getMapId() == 784){//�̸�������
							l1skilluse.handleCommands(pc,
									L1SkillId.STATUS_TIKAL_BOSSDIE, pc.getId(),
									pc.getX(), pc.getY(), null, 0,
									L1SkillUse.TYPE_GMBUFF);
							pc.sendPackets(new S_SystemMessage("���극���� �������� ���ž�� �ູ�� 60�� �����˴ϴ�."));
							pc.setBuffnoch(0);
						}
						}
						Mapdrop(mon);
					}
					if(_bosstype == 400016 || _bosstype == 400017){//�׺�����
						Mapdrop(mon);
					}

					 if(_bosstype == 4039020||_bosstype == 4039000 ||_bosstype == 4039021||_bosstype == 4039006
							 ||_bosstype == 9170 ||_bosstype == 9171){//1~2���븰�巹�̵�
						for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
							if(mon.getMapId() == pc.getMapId()){
								pc.getInventory().storeItem(439115, 1);
								pc.sendPackets(new S_SystemMessage("�޾Ƴ� �巡���� ������ ���� �Ǿ����ϴ�."));//�޼���
							}
						}
					 }
				}		
			} catch (Exception e) {
				System.out.println(e);
			}
		}	
					/**�������ڵ��й�**/
		private void Mapdrop(L1NpcInstance npc){
			L1Inventory inventory = npc.getInventory();
			L1ItemInstance item;
			L1Inventory targetInventory = null;
			L1PcInstance player;
			Random random = new Random();
			L1PcInstance acquisitor;
			FastTable<L1PcInstance> acquisitorList = new FastTable<L1PcInstance>();
			L1PcInstance[] pclist = L1World.getInstance().getAllPlayersToArray();
			for(L1PcInstance temppc : pclist){
				if(temppc.getMapId() == npc.getMapId())
					acquisitorList.add(temppc);
			}
			for (int i = inventory.getSize(); i > 0; i--) {
				item = inventory.getItems().get(0);

				if (item.getItem().getType2() == 0 && item.getItem().getType() == 2) {
					item.setNowLighting(false);
				}
				acquisitor = acquisitorList.get(random.nextInt(acquisitorList.size()));
				if (acquisitor.getInventory().checkAddItem(item,item.getCount()) == L1Inventory.OK) {
						targetInventory = acquisitor.getInventory();
						player = acquisitor;
						L1ItemInstance l1iteminstance = player.getInventory().findItemId(L1ItemId.ADENA); // ����
						if (l1iteminstance != null&& l1iteminstance.getCount() > 2000000000) {
								targetInventory = L1World.getInstance().getInventory(acquisitor.getX(),acquisitor.getY(),acquisitor.getMapId()); // ���� ��
								player.sendPackets(new S_ServerMessage(166,"�����ϰ� �ִ� �Ƶ���","2,000,000,000�� �ʰ��ϰ� �ֽ��ϴ�."));
						}else{
							for(L1PcInstance temppc : acquisitorList){
									temppc.sendPackets(new S_ServerMessage(813, npc.getName(), item.getLogName(), player.getName()));
							}
						}
				} else {
						targetInventory = L1World.getInstance().getInventory(acquisitor.getX(),acquisitor.getY(),acquisitor.getMapId()); // ���� ��
				}
				inventory.tradeItem(item, item.getCount(), targetInventory);
			}
			npc.getLight().turnOnOffLight();
		}
	}
	/**�������ڵ��й�**/
	public void dragonportalspawn(int npcId, int x, int y, short mapid, int heading, int 
			   timeMinToDelete) {
			     try {
			      L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(npcId);
			      npc.setId(ObjectIdFactory.getInstance().nextId());
			      npc.setMap(mapid);
			      npc.setX(x);
			      npc.setY(y);
			      npc.setHomeX(npc.getX());
			      npc.setHomeY(npc.getY());
			      npc.getMoveState().setHeading(heading);
			      L1World.getInstance(). storeObject(npc);
			      L1World.getInstance(). addVisibleObject(npc);
			      if (0 < timeMinToDelete) {
			       L1NpcDeleteTimer timer = new L1NpcDeleteTimer(npc,
			         timeMinToDelete * 60 * 1000);
			       timer.begin();
			      }
			     } catch (Exception e) {
			      _log.log(Level.SEVERE, "L1MonsterInstance[]Error", e);
			     }
			    }
	/** ������ ����
	 **/
	private void diePaPoo(L1Character lastAttacker) {
		setDeathProcessing(true);
		setCurrentHp(0);
		setDead(true);
		getMap().setPassable(getLocation(), true);
		startChat(CHAT_TIMING_DEAD);
		setDeathProcessing(false);
		setExp(0);
		setKarma(0);
		allTargetClear();
		int transformGfxId = getNpcTemplate().getTransformGfxId();
		if (transformGfxId > 0)
			Broadcaster.broadcastPacket(this, new S_SkillSound(getId(),
					transformGfxId));
		setActionStatus(ActionCodes.ACTION_Die);
		Broadcaster.broadcastPacket(this, new S_DoActionGFX(getId(),
				ActionCodes.ACTION_Die));
		deleteMe();
		GeneralThreadPool.getInstance().schedule(new PaPooTransTimer(this),
				20 * 1000);

	}
	private static class PaPooTransTimer implements Runnable {
		L1NpcInstance _npc;

		private PaPooTransTimer(L1NpcInstance some) {
			_npc = some;
		}

		public void run() {
			L1SpawnUtil.spawn2(_npc.getX(), _npc.getY(), (short) _npc.getMap().getId(), _npc.getNpcTemplate().getTransformId(), 0, 0, 0);
		}
	}
	private static class GiranTransTimer implements Runnable{
		L1NpcInstance _npc;

		private GiranTransTimer(L1NpcInstance some) {
			_npc = some;
		}

		public void run() {
			L1SpawnUtil.spawn2(_npc.getX(), _npc.getY(), (short) _npc.getMap().getId(), _npc.getNpcTemplate().getTransformId(), 0, 0, 0);
		}
	}
	/**���巹�̵�*/
	private void diePind(L1Character lastAttacker) {
		//System.out.println("c opcode");
		setDeathProcessing(true);
		setCurrentHp(0);
		setDead(true);
		getMap().setPassable(getLocation(), true);
		startChat(CHAT_TIMING_DEAD);
		setDeathProcessing(false);
		setExp(0);
		setKarma(0);
		allTargetClear();
		int transformGfxId = getNpcTemplate().getTransformGfxId();
		if (transformGfxId > 0)
			Broadcaster.broadcastPacket(this, new S_SkillSound(getId(),
					transformGfxId));
		setActionStatus(ActionCodes.ACTION_Die);
		Broadcaster.broadcastPacket(this, new S_DoActionGFX(getId(),
				ActionCodes.ACTION_Die));
		deleteMe();
		GeneralThreadPool.getInstance().schedule(new PindTransTimer(this),
				20 * 1000);

	}
	private static class PindTransTimer implements Runnable {
		L1NpcInstance _npc;

		private PindTransTimer(L1NpcInstance some) {
			_npc = some;
		}

		public void run() {
			L1SpawnUtil.spawn2(_npc.getX(), _npc.getY(), (short) _npc.getMap().getId(), _npc.getNpcTemplate().getTransformId(), 0, 0, 0);
		}
	}
	/**���巹�̵�*/
	private boolean isGDMonster() {
		int id = getNpcTemplate().get_npcId();
		if ((id >= 4037100 && id <= 4037102)
				|| (id >= 4037200 && id <= 4037202)
				|| (id >= 4037400 && id <= 4037403))
			return true;
		return false;
	}
	private boolean isAntharas() {
		int id = getNpcTemplate().get_npcId();
		if (id == 4039020 || id == 4039021) return true;
			return false;
	}
	private boolean isPapoo() {
		int id = getNpcTemplate().get_npcId();
		if (id == 4039000 || id == 4039006)
			return true;
		return false;
	}
	private boolean isPind() {//���巹�̵�
		int id = getNpcTemplate().get_npcId();
		if (id == 9170 || id == 9171)
			return true;
		return false;
	}

	private boolean ismarble() {
		int id = getNpcTemplate().get_npcId();
		if (id == 4039001)
			return true;
		return false;
	}

	private boolean ismarble2() {
		int id = getNpcTemplate().get_npcId();
		if (id == 4039002)
			return true;
		return false;
	}
	private void distributeExpDropKarma(L1Character lastAttacker) {
		if (lastAttacker == null) {
			return;
		}
		L1PcInstance pc = null;
		if (lastAttacker instanceof L1PcInstance) {
			pc = (L1PcInstance) lastAttacker;
		} else if (lastAttacker instanceof L1PetInstance) {
			pc = (L1PcInstance) ((L1PetInstance) lastAttacker).getMaster();
		} else if (lastAttacker instanceof L1SummonInstance) {
			pc = (L1PcInstance) ((L1SummonInstance) lastAttacker).getMaster();
		}

		if (pc != null) {
			FastTable<L1Character> targetList = _hateList.toTargetFastTable();
			FastTable<Integer> hateList = _hateList.toHateFastTable();			
			if (pc != null && pc.getRobotAi() == null) {
				int exp = getExp();
				CalcExp.calcExp(pc, getId(), targetList, hateList, exp);
				if (isDead()) {
					distributeDrop(pc);
					giveKarma(pc);
				}
			}
		} else if (lastAttacker instanceof L1EffectInstance) {
			FastTable<L1Character> targetList = _hateList.toTargetFastTable();
			FastTable<Integer> hateList = _hateList.toHateFastTable();
			if (hateList.size() != 0) {
				int maxHate = 0;
				for (int i = hateList.size() - 1; i >= 0; i--) {
					if (maxHate < ((Integer) hateList.get(i))) {
						maxHate = (hateList.get(i));
						lastAttacker = targetList.get(i);
					}
				}
				if (lastAttacker instanceof L1PcInstance) {
					pc = (L1PcInstance) lastAttacker;
				} else if (lastAttacker instanceof L1PetInstance) {
					pc = (L1PcInstance) ((L1PetInstance) lastAttacker)
							.getMaster();
				} else if (lastAttacker instanceof L1SummonInstance) {
					pc = (L1PcInstance) ((L1SummonInstance) lastAttacker)
							.getMaster();
				}
				int exp = getExp();
				CalcExp.calcExp(pc, getId(), targetList, hateList, exp);
				if (isDead()) {
					distributeDrop(pc);
					giveKarma(pc);
				}
			}
		}
	}

	private void distributeDrop(L1PcInstance pc) {
		FastTable<L1Character> dropTargetList = _dropHateList
				.toTargetFastTable();
		FastTable<Integer> dropHateList = _dropHateList.toHateFastTable();
		try {
			int npcId = getNpcTemplate().get_npcId();
			if (npcId != 45640
					|| (npcId == 45640 && getGfxId().getTempCharGfx() == 2332)) {
				DropTable.getInstance().dropShare(L1MonsterInstance.this,
						dropTargetList, dropHateList, pc);
			}
		} catch (Exception e) {
			
			//_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	private void giveKarma(L1PcInstance pc) {
		int karma = getKarma();
		if (karma != 0) {
			int karmaSign = Integer.signum(karma);
			int pcKarmaLevel = pc.getKarmaLevel();
			int pcKarmaLevelSign = Integer.signum(pcKarmaLevel);
			if (pcKarmaLevelSign != 0 && karmaSign != pcKarmaLevelSign) {
				karma *= 5;
			}
			pc.addKarma((int) (karma * Config.RATE_KARMA));
		}
	}

	private void giveUbSeal() {
		if (getUbSealCount() != 0) {
			L1UltimateBattle ub = UBTable.getInstance().getUb(getUbId());
			if (ub != null) {
				for (L1PcInstance pc : ub.getMembersArray()) {
					if (pc != null && !pc.isDead() && !pc.isGhost()) {
						if (_random.nextInt(10) <= 2) {
							pc.getInventory().storeItem(
									L1ItemId.UB_WINNER_PIECE, 1);
						}
						pc.getInventory().storeItem(Config.UB_SEAL_ITEM,
								getUbSealCount());
						pc.sendPackets(new S_ServerMessage(403, "$5448"));
					}
				}
			}
		}
	}

	public int get_storeDroped() {
		return _storeDroped;
	}

	public void set_storeDroped(int i) {
		_storeDroped = i;
	}

	private int _ubSealCount = 0;

	public int getUbSealCount() {
		return _ubSealCount;
	}

	public void setUbSealCount(int i) {
		_ubSealCount = i;
	}

	private int _ubId = 0; // UBID

	public int getUbId() {
		return _ubId;
	}

	public void setUbId(int i) {
		_ubId = i;
	}

	private void hide() {
		int npcid = getNpcTemplate().get_npcId();
		if (npcid == 45061 || npcid == 45161 || npcid == 45181
				|| npcid == 45455) {
			if (getMaxHp() / 3 > getCurrentHp()) {
				int rnd = _random.nextInt(10);
				if (1 > rnd) {
					allTargetClear();
					setHiddenStatus(HIDDEN_STATUS_SINK);
					Broadcaster.broadcastPacket(this, new S_DoActionGFX(
							getId(), ActionCodes.ACTION_Hide));
					setActionStatus(13);
					Broadcaster.broadcastPacket(this, new S_NPCPack(this));
				}
			}
		} else if (npcid == 45682) {
			if (getMaxHp() / 3 > getCurrentHp()) {
				int rnd = _random.nextInt(50);
				if (1 > rnd) {
					allTargetClear();
					setHiddenStatus(HIDDEN_STATUS_SINK);
					Broadcaster.broadcastPacket(this, new S_DoActionGFX(
							getId(), ActionCodes.ACTION_AntharasHide));
					setActionStatus(20);
					Broadcaster.broadcastPacket(this, new S_NPCPack(this));
				}
			}
		} else if (npcid == 45067 || npcid == 45264 || npcid == 45452
				|| npcid == 45090 || npcid == 45321 || npcid == 45445) {
			if (getMaxHp() / 3 > getCurrentHp()) {
				int rnd = _random.nextInt(10);
				if (1 > rnd) {
					allTargetClear();
					setHiddenStatus(HIDDEN_STATUS_FLY);
					Broadcaster.broadcastPacket(this, new S_DoActionGFX(
							getId(), ActionCodes.ACTION_Moveup));
					setActionStatus(4);
					Broadcaster.broadcastPacket(this, new S_NPCPack(this));
				}
			}
		} else if (npcid == 45681) {
			if (getMaxHp() / 3 > getCurrentHp()) {
				int rnd = _random.nextInt(50);
				if (1 > rnd) {
					allTargetClear();
					setHiddenStatus(HIDDEN_STATUS_FLY);
					Broadcaster.broadcastPacket(this, new S_DoActionGFX(
							getId(), ActionCodes.ACTION_Moveup));
					setActionStatus(11);
					Broadcaster.broadcastPacket(this, new S_NPCPack(this));
				}
			}
		}
	}

	public void initHide() {
		int npcid = getNpcTemplate().get_npcId();
		if (npcid == 45061 || npcid == 45161 || npcid == 45181
				|| npcid == 45455 || npcid == 400000 || npcid == 400001) {
			int rnd = _random.nextInt(3);
			if (1 > rnd) {
				setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_SINK);
				setActionStatus(13);
			}
		} else if (npcid == 45045 || npcid == 45126 || npcid == 45134
				|| npcid == 45281) {
			int rnd = _random.nextInt(3);
			if (1 > rnd) {
				setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_SINK);
				setActionStatus(4);
			}
		} else if (npcid == 45067 || npcid == 45264 || npcid == 45452
				|| npcid == 45090 || npcid == 45321 || npcid == 45445) {
			setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_FLY);
			setActionStatus(4);
		} else if (npcid == 45681) {
			setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_FLY);
			setActionStatus(11);
		}
	}

	public void initHideForMinion(L1NpcInstance leader) {
		int npcid = getNpcTemplate().get_npcId();
		if (leader.getHiddenStatus() == L1NpcInstance.HIDDEN_STATUS_SINK) {
			if (npcid == 45061 || npcid == 45161 || npcid == 45181
					|| npcid == 45455 || npcid == 400000 || npcid == 400001) {
				setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_SINK);
				setActionStatus(13);
			} else if (npcid == 45045 || npcid == 45126 || npcid == 45134
					|| npcid == 45281) {
				setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_SINK);
				setActionStatus(4);
			}
		} else if (leader.getHiddenStatus() == L1NpcInstance.HIDDEN_STATUS_FLY) {
			if (npcid == 45067 || npcid == 45264 || npcid == 45452
					|| npcid == 45090 || npcid == 45321 || npcid == 45445) {
				setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_FLY);
				setActionStatus(4);
			} else if (npcid == 45681) {
				setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_FLY);
				setActionStatus(11);
			}
		}
	}

	public void getMarble(L1NpcInstance npc) {
		_attacker = npc;
		_attacker.marble.remove("��������");
		_attacker.marble2.remove("�ź��ѿ�������");
	}

	@Override
	protected void transform(int transformId) {
		super.transform(transformId);
		getInventory().clearItems();
		DropTable.getInstance().setDrop(this, getInventory());
		getInventory().shuffle();
	}
}
