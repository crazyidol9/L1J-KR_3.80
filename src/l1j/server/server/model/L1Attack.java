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
package l1j.server.server.model;

import static l1j.server.server.model.skill.L1SkillId.ABSOLUTE_BARRIER;
import static l1j.server.server.model.skill.L1SkillId.ANTA_MAAN;
import static l1j.server.server.model.skill.L1SkillId.ARMOR_BRAKE;
import static l1j.server.server.model.skill.L1SkillId.BERSERKERS;
import static l1j.server.server.model.skill.L1SkillId.BIRTH_MAAN;
import static l1j.server.server.model.skill.L1SkillId.BURNING_SLASH;
import static l1j.server.server.model.skill.L1SkillId.BURNING_SPIRIT;
import static l1j.server.server.model.skill.L1SkillId.BURNING_WEAPON;
import static l1j.server.server.model.skill.L1SkillId.COUNTER_BARRIER;
import static l1j.server.server.model.skill.L1SkillId.DOUBLE_BRAKE;
import static l1j.server.server.model.skill.L1SkillId.DRAGON_SKIN;
import static l1j.server.server.model.skill.L1SkillId.EARTH_BIND;
import static l1j.server.server.model.skill.L1SkillId.ELEMENTAL_FIRE;
import static l1j.server.server.model.skill.L1SkillId.ENCHANT_VENOM;
import static l1j.server.server.model.skill.L1SkillId.FEAR;
import static l1j.server.server.model.skill.L1SkillId.FIRE_BLESS;
import static l1j.server.server.model.skill.L1SkillId.FIRE_WEAPON;
import static l1j.server.server.model.skill.L1SkillId.FREEZING_BLIZZARD;
import static l1j.server.server.model.skill.L1SkillId.ICE_LANCE;
import static l1j.server.server.model.skill.L1SkillId.IMMUNE_TO_HARM;
import static l1j.server.server.model.skill.L1SkillId.IllUSION_AVATAR;
import static l1j.server.server.model.skill.L1SkillId.LIFE_MAAN;
import static l1j.server.server.model.skill.L1SkillId.MIRROR_IMAGE;
import static l1j.server.server.model.skill.L1SkillId.MOB_BASILL;
import static l1j.server.server.model.skill.L1SkillId.MOB_COCA;
import static l1j.server.server.model.skill.L1SkillId.PAP_FIVEPEARLBUFF;
import static l1j.server.server.model.skill.L1SkillId.PAP_MAGICALPEARLBUFF;
import static l1j.server.server.model.skill.L1SkillId.PATIENCE;
import static l1j.server.server.model.skill.L1SkillId.REDUCTION_ARMOR;
import static l1j.server.server.model.skill.L1SkillId.SHAPE_MAAN;
import static l1j.server.server.model.skill.L1SkillId.SILENCE;
import static l1j.server.server.model.skill.L1SkillId.SOUL_OF_FLAME;
import static l1j.server.server.model.skill.L1SkillId.SPECIAL_COOKING;
import static l1j.server.server.model.skill.L1SkillId.STATUS_CURSE_BARLOG;
import static l1j.server.server.model.skill.L1SkillId.STATUS_CURSE_YAHEE;
import static l1j.server.server.model.skill.L1SkillId.STATUS_HOLY_MITHRIL_POWDER;
import static l1j.server.server.model.skill.L1SkillId.STATUS_HOLY_WATER;
import static l1j.server.server.model.skill.L1SkillId.STATUS_HOLY_WATER_OF_EVA;
import static l1j.server.server.model.skill.L1SkillId.UNCANNY_DODGE;
import static l1j.server.server.model.skill.L1SkillId.VALA_MAAN;

import java.util.Random;

import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.TimeController.WarTimeController;
import l1j.server.server.datatables.WeaponAddDamage;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.gametime.GameTimeClock;
import l1j.server.server.model.poison.L1DamagePoison;
import l1j.server.server.model.poison.L1ParalysisPoison;
import l1j.server.server.model.poison.L1SilencePoison;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_AttackMissPacket;
import l1j.server.server.serverpackets.S_AttackPacket;
import l1j.server.server.serverpackets.S_AttackPacketForNpc;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_UseArrowSkill;
import l1j.server.server.serverpackets.S_UseAttackSkill;
import l1j.server.server.types.Point;

public class L1Attack {

	private L1PcInstance _pc = null;

	private L1Character _target = null;

	private L1PcInstance _targetPc = null;

	private L1NpcInstance _npc = null;

	private L1NpcInstance _targetNpc = null;

	private final int _targetId;

	private int _targetX;

	private int _targetY;

	private int _statusDamage = 0;

	private static final Random _random = new Random();

	private int _hitRate = 0;

	private int _calcType;

	private static final int PC_PC = 1;

	private static final int PC_NPC = 2;

	private static final int NPC_PC = 3;

	private static final int NPC_NPC = 4;

	public boolean _isHit = false;

	private int _damage = 0;

	private int _drainMana = 0;

	/** ������ ���� * */

	private int _drainHp = 0;

	/** ������ ���� * */

	private int _attckGrfxId = 0;

	private int _attckActId = 0;

	// �����ڰ� �÷��̾��� ����� ���� ����
	private L1ItemInstance weapon = null;

	private int _weaponId = 0;

	private int _weaponType = 0;

	private int _weaponType1 = 0;

	private int _weaponAddHit = 0;

	private int _weaponAddDmg = 0;

	private int _weaponSmall = 0;

	private int _weaponLarge = 0;

	private int _weaponRange = 1;

	private int _weaponBless = 1;

	private int _weaponEnchant = 0;

	private int _weaponMaterial = 0;

	private int _weaponAttrEnchantLevel = 0;

	private int _weaponDoubleDmgChance = 0;

	private int _attackType = 0;

	private L1ItemInstance _arrow = null;

	private L1ItemInstance _sting = null;

	private int _leverage = 10; // 1/10��� ǥ���Ѵ�.

	public void setLeverage(int i) {
		_leverage = i;
	}

	private int getLeverage() {
		return _leverage;
	}

	// �����ڰ� �÷��̾��� ����� �������ͽ��� ���� ����
	private static final int[] strHit = { -2, -2, -2, -2, -2, -2, -2, -2, -1,
			-1, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6, 7, 7, 7, 8,
			8, 8, 9, 9, 9, 10, 10, 10, 11, 11, 11, 12, 12, 12, 13, 13, 13, 14,
			14, 14, 15, 15, 15, 16, 16, 16, 17, 17 };

	private static final int[] dexHit = { -2, -2, -2, -2, -2, -2, -1, -1, 0, 0,
			1, 1, 2, 2, 3, 3, 4, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,
			17, 18, 19, 19, 19, 20, 20, 20, 21, 21, 21, 22, 22, 22, 23, 23, 23,
			24, 24, 24, 25, 25, 25, 26, 26, 26, 27, 27, 27, 28 };

	private static final int[] strDmg = new int[128];

	static {
		// STR ������ ����
		int dmg = -2;
		for (int str = 0; str <= 127; str++) {
			if (str == 9 || str == 11 || str == 13 || str == 15 || str == 17
					|| str == 19 || str == 21 || str == 23 || str == 26
					|| str == 29 || str == 31 || str == 33 || str == 34
					|| str == 35 || str == 39 || str == 43 || str == 47
					|| str == 49 || str == 50) { // ��Ÿ�� �����ϴ� �� ��ġ
				dmg++;
			} else if (str > 50 && str % 4 == 2) { // 50�̻� ���� 127������ 4���٣�1 ����
				// (��Ȯ��)
				dmg++;
			}
			strDmg[str] = dmg;
		}

	}

	private static final int[] dexDmg = new int[128];

	static {
		// DEX ������ ����
		int dmg = 0;
		for (int dex = 0; dex <= 127; dex++) {
			if (dex == 15 || dex == 16 || dex == 17 || dex == 18 || dex == 21
					|| dex == 24 || dex == 27 || dex == 30 || dex == 33
					|| dex == 36 || dex == 40 || dex == 44) { // ��Ÿ�� �����ϴ� ����
				// ��ġ
				dmg++;
			} else if (dex >= 48 && dex % 4 == 0) { // 48���� 127������ 4���٣�1 ����
				// (��Ȯ��)
				dmg++;
			}
			dexDmg[dex] = dmg;
		}
	}


	private static final int[] IntDmg = new int[128]; // Ű��ũ ��Ʈ

	static {
		// Int ������ ����
		for (int Int = 0; Int <= 14; Int++) { // 0~14�� 0
			IntDmg[Int] = -1;
		}
		IntDmg[15] = 2; // ��Ʈ�� ���ϴµ�����
		IntDmg[16] = 3;
		IntDmg[17] = 4;
		IntDmg[18] = 5;
		IntDmg[19] = 6;
		IntDmg[20] = 7;
		IntDmg[21] = 8;
		IntDmg[22] = 9;
		IntDmg[23] = 10;
		IntDmg[24] = 11;
		IntDmg[25] = 12;
		IntDmg[26] = 13;
		IntDmg[27] = 14;
		IntDmg[28] = 15;
		IntDmg[29] = 16;
		IntDmg[30] = 17;
		IntDmg[31] = 18;
		IntDmg[32] = 19;
		IntDmg[33] = 20;
		IntDmg[34] = 21;
		IntDmg[35] = 22;
		IntDmg[36] = 23;
		IntDmg[37] = 24;
		IntDmg[38] = 25;
		IntDmg[39] = 26;
		int dmg = 27;
		for (int Int = 40; Int <= 127; Int++) { // 40~127�� 2���٣�2 //#
			if (Int % 2 == 1) {
				dmg += 3;
			}
			IntDmg[Int] = dmg;
		}
	}


	public void setActId(int actId) {
		_attckActId = actId;
	}

	public void setGfxId(int gfxId) {
		_attckGrfxId = gfxId;
	}

	public int getActId() {
		return _attckActId;
	}

	public int getGfxId() {
		return _attckGrfxId;
	}

	public L1Attack(L1Character attacker, L1Character target) {
		if (attacker instanceof L1PcInstance) {
			_pc = (L1PcInstance) attacker;
			if (target instanceof L1PcInstance) {
				_targetPc = (L1PcInstance) target;
				_calcType = PC_PC;
			} else if (target instanceof L1NpcInstance) {
				_targetNpc = (L1NpcInstance) target;
				_calcType = PC_NPC;
			}
			// ���� ������ ���
			weapon = _pc.getWeapon();
			if (weapon != null) {
				_weaponId = weapon.getItem().getItemId();
				_weaponType = weapon.getItem().getType1();
				_weaponAddHit = weapon.getItem().getHitModifier()
						+ weapon.getHitByMagic();
				_weaponAddDmg = weapon.getItem().getDmgModifier()
						+ weapon.getDmgByMagic()+ weapon.getItem().getaddDmg();
				_weaponType1 = weapon.getItem().getType();
				_weaponSmall = weapon.getItem().getDmgSmall();
				_weaponLarge = weapon.getItem().getDmgLarge();
				_weaponRange = weapon.getItem().getRange();
				_weaponBless = weapon.getItem().getBless();
				if (_weaponType != 20 && _weaponType != 62) {
					_weaponEnchant = weapon.getEnchantLevel()
							- weapon.get_durability(); // �ջ�� ���̳ʽ�
				} else {
					_weaponEnchant = weapon.getEnchantLevel();
				}
				_weaponMaterial = weapon.getItem().getMaterial();
				if (_weaponType == 20) { // ȭ���� ���
					_arrow = _pc.getInventory().getArrow();
					if (_arrow != null) {
						_weaponBless = _arrow.getItem().getBless();
						_weaponMaterial = _arrow.getItem().getMaterial();
					}
				}
				if (_weaponType == 62) { // ������ ���
					_sting = _pc.getInventory().getSting();
					if (_sting != null) {
						_weaponBless = _sting.getItem().getBless();
						_weaponMaterial = _sting.getItem().getMaterial();
					}
				}
				_weaponDoubleDmgChance = weapon.getItem().getDoubleDmgChance();
				_weaponAttrEnchantLevel = weapon.getAttrEnchantLevel();
			}
			// �������ͽ��� ���� �߰� ������ ����
			if (_weaponType == 20) { // Ȱ�� ���� DEXġ ����
				_statusDamage = dexDmg[_pc.getAbility().getTotalDex()];
			} else {
				_statusDamage = strDmg[_pc.getAbility().getTotalStr()];
			}
		} else if (_weaponId == 450013 || _weaponId == 410003
				|| _weaponId == 450008 || _weaponId == 410004
				|| _weaponId == 450006 || _weaponId == 450009 || _weaponId == 45000600
				|| _weaponId == 450010 || _weaponId == 450011
				|| _weaponId == 450012 || _weaponId == 4500081
				|| _weaponId == 4500091 || _weaponId == 4500101
				|| _weaponId == 4500111 || _weaponId == 121
				|| _weaponId == 124|| _weaponId == 421) { // Ű��ũ��
			// ���
			// INTġ
			// ����
			// �׺�����,����,������
			// ����
			//_statusDamage = IntDmg[_pc.getAbility().getTotalInt()];
		} else if (attacker instanceof L1NpcInstance) {
			_npc = (L1NpcInstance) attacker;
			if (target instanceof L1PcInstance) {
				_targetPc = (L1PcInstance) target;
				_calcType = NPC_PC;
			} else if (target instanceof L1NpcInstance) {
				_targetNpc = (L1NpcInstance) target;
				_calcType = NPC_NPC;
			}
		}
		_target = target;
		_targetId = target.getId();
		_targetX = target.getX();
		_targetY = target.getY();
	}

	/* ����������������� ���� ���� ����������������� */

	public boolean calcHit() {
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			if (_pc == null || _target == null)
				return _isHit;
			if (_weaponRange != -1) {
				if (_pc.getLocation()
						.getTileLineDistance(_target.getLocation()) > _weaponRange + 1) {
					_isHit = false;
					return _isHit;
				}
				if (_weaponType1 == 17||_weaponType1 == 19) {
					_isHit = true;
					return _isHit;
				}
			} else {
				if (!_pc.getLocation().isInScreen(_target.getLocation())) {
					_isHit = false;
					return _isHit;
				}
			}
			if (_weaponType == 20 && _weaponId != 190 && _weaponId != 11011
					&& _weaponId != 11012 && _weaponId != 11013 && _arrow == null) { // �߰�
				return _isHit = false; // ȭ���� ���� ���� �̽�
			} else if (_weaponType == 62 && _sting == null) {
				/** ���� ȭ�� �վ� ���� **/
				if (_weaponType == 20 && _weaponType == 62) {
					if (_pc.getX() == 33636 && _pc.getY() == 32679
							&& _pc.getMapId() == 4) {
						return _isHit = false;
					}else if (_pc.getX() == 33635 && _pc.getY() == 32680
							&& _pc.getMapId() == 4) {
						return _isHit = false;
					}else if (_pc.getX() == 33634 && _pc.getY() == 32681
							&& _pc.getMapId() == 4) {
						return _isHit = false;
					}else if (_pc.getX() == 33633 && _pc.getY() == 32682
							&& _pc.getMapId() == 4) {
						return _isHit = false;
					}else if (_pc.getX() == 33627 && _pc.getY() == 32676
							&& _pc.getMapId() == 4) {
						return _isHit = false;
					}else if (_pc.getX() == 33628 && _pc.getY() == 32675
							&& _pc.getMapId() == 4) {
						return _isHit = false;
					}else if (_pc.getX() == 33629 && _pc.getY() == 32674
							&& _pc.getMapId() == 4) {
						return _isHit = false;
					}else if (_pc.getX() == 33630 && _pc.getY() == 32673
							&& _pc.getMapId() == 4) {
						return _isHit = false;
					}
				}
				/** ���� ȭ�� �վ� ���� **/
				return _isHit = false; // ������ ���� ���� �̽�
			} else if (!CharPosUtil.glanceCheck(_pc, _targetX, _targetY)) {
				return _isHit = false; // �����ڰ� �÷��̾��� ���� ��ֹ� ����
			} else if (_weaponId == 247 || _weaponId == 248 || _weaponId == 249) {
				return _isHit = false; // �÷��� ��B~C ���� ��ȿ
			} else if (_calcType == PC_PC) {
				if (CharPosUtil.getZoneType(_pc) == 1
						|| CharPosUtil.getZoneType(_targetPc) == 1) {
					return _isHit = false;
				}
				return _isHit = calcPcPcHit();
			} else if (_calcType == PC_NPC) {
				return _isHit = calcPcNpcHit();
			}
		} else if (_calcType == NPC_PC) {
			return _isHit = calcNpcPcHit();
		} else if (_calcType == NPC_NPC) {
			return _isHit = calcNpcNpcHit();
		}
		return _isHit;
	}

	// �ܡܡܡ� �÷��̾�κ��� �÷��̾�� ���� ���� �ܡܡܡ�
	/*
	 * PC���� ������ =(PC�� Lv��Ŭ���� ������STR ������DEX ���������� ������DAI�� �ż�/2������ ����)��0.68��10
	 * �̰����� ����� ��ġ�� �ڽ��� �ִ� ����(95%)�� �ִ� ���� �� �� �ִ� ����� PC�� AC �ű�κ��� ����� PC�� AC��
	 * 1������ ������ �ڸ������κ��� 1��� ���� �ּ� ������5% �ִ� ������95%
	 */
	private boolean calcPcPcHit() {
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(ABSOLUTE_BARRIER)
				|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(ICE_LANCE)
				|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(
						FREEZING_BLIZZARD))
			return false;

		_hitRate = _pc.getLevel();

		if (_pc.getAbility().getTotalStr() > 59) {
			_hitRate += (strHit[58]);
		} else {
			_hitRate += (strHit[_pc.getAbility().getTotalStr() - 1]);
		}

		if (_pc.getAbility().getTotalDex() > 60) {
			_hitRate += (dexHit[59]);
		} else {
			_hitRate += (dexHit[_pc.getAbility().getTotalDex() - 1]);
		}

		if (_weaponType != 20 && _weaponType != 62) {
			_hitRate += _weaponAddHit + _pc.getHitup() + _pc.getHitupByArmor()
					+ (_weaponEnchant / 2);
		} else {
			_hitRate += _weaponAddHit + _pc.getBowHitup()
					+ _pc.getBowHitupByArmor() + _pc.getBowHitupByDoll()
					+ (_weaponEnchant / 2) + _pc.getKnifeHitupByDoll();
		}
		if (_weaponType == 20 || _weaponType == 62) {
			Object[] dollList = _pc.getDollList().values().toArray(); // ����������
			// ���� �߰�
			// ������
			// (���Ÿ�)

			for (Object dollObject : dollList) {
				L1DollInstance doll = (L1DollInstance) dollObject;

				_hitRate += _pc.getBowHitup() + doll.getBowhitRateByDoll();
			}
		}
		int attackerDice = _random.nextInt(20) + 1 + _hitRate - 10;
		if (_weaponType != 20 && _weaponType != 62) { // ���Ÿ��� ���� �� �ް�

			if (_targetPc.getSkillEffectTimerSet()
					.hasSkillEffect(UNCANNY_DODGE)) {// ����
				attackerDice -= 4.5;
			}
		}
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(MIRROR_IMAGE)) {//�̷��̹���
			attackerDice -= 5;
	    }
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(FEAR)) {//�Ǿ�
			attackerDice += 5;
		}
		int defenderDice = 0;

		int defenderValue = (int) (_targetPc.getAC().getAc() * 1.5) * -1;

		if (_targetPc.getAC().getAc() >= 0) {
			defenderDice = 10 - _targetPc.getAC().getAc();
		} else if (_targetPc.getAC().getAc() < 0) {
			defenderDice = 10 + _random.nextInt(defenderValue) + 1;
		}

		int fumble = _hitRate - 9;
		int critical = _hitRate + 10;

		if (attackerDice <= fumble) {
			_hitRate = 0;
		} else if (attackerDice >= critical) {
			_hitRate = 100;
		} else {
			if (attackerDice > defenderDice) {
				_hitRate = 100;
			} else if (attackerDice <= defenderDice) {
				_hitRate = 0;
			}
		}
		if(_calcType == PC_PC){
			if(_targetPc.getLevel() < Config.MAX_LEVEL || _pc.getLevel() < Config.MAX_LEVEL){ //�űԺ�ȣ
				_hitRate -= 100;
           } 
         }
		if(_calcType == PC_PC){
		if(_targetPc.getMapId() == 777 ||_targetPc.getMapId() == 778||_targetPc.getMapId() == 779 ||_targetPc.getMapId() == 56){ //��������������̷κ���
			_hitRate -= 100;
			}
		}
		if(_calcType == PC_PC){
		if(_pc.getMapId() == 777 ||_pc.getMapId() == 778||_pc.getMapId() == 779 ||_pc.getMapId() == 56){ //��������������̷κ���
			_hitRate -= 100;
			}
		}
		
		
		if(_pc.getMapId() == 5153){
			if(_pc.get_DuelLine() == _targetPc.get_DuelLine()){
				_pc.sendPackets(new S_SystemMessage("��Ʋ�� ���� ���γ����� ������ �Ұ����մϴ�.")); 
				_hitRate = 0;
			}
		}
		
		/**������ �ƴҶ� ������ �ȿ��� ���� ����**/
		int castle_id2 = L1CastleLocation.getCastleIdByArea(_pc);
		if (0 < castle_id2) {
			if (!WarTimeController.getInstance().isNowWar(castle_id2)) {
				_pc.sendPackets(new S_SystemMessage("\\fY�����忡���� PK�� ���ѵ˴ϴ�."));
				_hitRate = 0;
			}
		}
		
		
		// [���� ȸ�� �κ�]����.����.����.ź��
		if (_targetPc != null) {
			if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(ANTA_MAAN)
					|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(
							LIFE_MAAN)
					|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(
							SHAPE_MAAN)
					|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(
							BIRTH_MAAN)) {
				int chance = _random.nextInt(100) + 1;
				if (chance < 5) {
					_hitRate = 0;
				}
			}
		}
		int rnd = _random.nextInt(100) + 1;
		if (_weaponType == 20 && _hitRate > rnd) { // Ȱ�� ���, ��Ʈ ���� ��쿡���� ER������
			// ȸ�Ǹ� ���� �ǽ��Ѵ�.
			return calcErEvasion();
		}

		return _hitRate >= rnd;
	}

	// �ܡܡܡ� �÷��̾�κ��� NPC ���� ���� ���� �ܡܡܡ�
	private boolean calcPcNpcHit() {
		// NPC���� ������
		// =(PC�� Lv��Ŭ���� ������STR ������DEX ���������� ������DAI�� �ż�/2������ ����)��5��{NPC�� AC��(-5)}
		_hitRate = _pc.getLevel();

		if (_pc.getAbility().getTotalStr() > 59) {
			_hitRate += (strHit[58]);
		} else {
			_hitRate += (strHit[_pc.getAbility().getTotalStr() - 1]);
		}

		if (_pc.getAbility().getTotalDex() > 60) {
			_hitRate += (dexHit[59]);
		} else {
			_hitRate += (dexHit[_pc.getAbility().getTotalDex() - 1]);
		}

		if (_weaponType != 20 && _weaponType != 62) {
			_hitRate += _weaponAddHit + _pc.getHitup() + _pc.getHitupByArmor()
					+ (_weaponEnchant / 2);
		} else {
			_hitRate += _weaponAddHit + _pc.getBowHitup()
					+ _pc.getBowHitupByArmor() + _pc.getBowHitupByDoll()
					+ (_weaponEnchant / 2) + +_pc.getKnifeHitupByDoll();
		}
		if (_weaponType == 20 || _weaponType == 62) {
			Object[] dollList = _pc.getDollList().values().toArray(); // ����������
			// ���� �߰�
			// ������
			// (���Ÿ�)

			for (Object dollObject : dollList) {
				L1DollInstance doll = (L1DollInstance) dollObject;

				_hitRate += _pc.getBowHitup() + doll.getBowhitRateByDoll();
			}
		}
		int attackerDice = _random.nextInt(20) + 1 + _hitRate - 10;

		if (_targetNpc.getSkillEffectTimerSet().hasSkillEffect(FEAR)) {
			attackerDice += 5;
		}

		int defenderDice = 10 - _targetNpc.getAC().getAc();

		int fumble = _hitRate - 9;
		int critical = _hitRate + 10;

		if (attackerDice <= fumble) {
			_hitRate = 0;
		} else if (attackerDice >= critical) {
			_hitRate = 100;
		} else {
			if (attackerDice > defenderDice) {
				_hitRate = 100;
			} else if (attackerDice <= defenderDice) {
				_hitRate = 0;
			}
		}
		int npcId = _targetNpc.getNpcTemplate().get_npcId();
		if (npcId >= 45912
				&& npcId <= 45915
				&& !_pc.getSkillEffectTimerSet().hasSkillEffect(
						STATUS_HOLY_WATER)) {
			_hitRate = 0;
		}
		if (npcId == 45916
				&& !_pc.getSkillEffectTimerSet().hasSkillEffect(
						STATUS_HOLY_MITHRIL_POWDER)) {
			_hitRate = 0;
		}
		if (npcId == 45941
				&& !_pc.getSkillEffectTimerSet().hasSkillEffect(
						STATUS_HOLY_WATER_OF_EVA)) {
			_hitRate = 0;
		}
		if (_pc.getMapId() != 666){
		if (!_pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_CURSE_BARLOG)
				&& (npcId == 45752 || npcId == 45753 || npcId == 7000007 || npcId == 7000008
						 || npcId == 7000009 || npcId == 70000010|| npcId == 7000011 
						 || npcId == 7000012 || npcId == 7000013 || npcId == 7000014
						 || npcId == 7000015 || npcId == 7000016)) {//���ο��Ǿ�����
			_hitRate = 0;
		}
		}
        if (_pc.getMapId() != 666){
		if (!_pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_CURSE_YAHEE)
			&& (npcId == 45675 || npcId == 81082 || npcId == 45625|| npcId == 45674 || npcId == 45685)) {
			_hitRate = 0;
		}
        }
		if (npcId >= 46068 && npcId <= 46091
				&& _pc.getGfxId().getTempCharGfx() == 6035) {
			_hitRate = 0;
		}
		if (npcId >= 46092 && npcId <= 46106
				&& _pc.getGfxId().getTempCharGfx() == 6034) {
			_hitRate = 0;
		}
		if (npcId == 4039001 
				&& !_pc.getSkillEffectTimerSet().hasSkillEffect(PAP_FIVEPEARLBUFF)){ // ��������
			_hitRate = 0;
		}
		if (npcId == 4039002 
				&& !_pc.getSkillEffectTimerSet().hasSkillEffect(PAP_MAGICALPEARLBUFF)){ // �ź�����
			_hitRate = 0;
		}
		int rnd = _random.nextInt(100) + 1;

		return _hitRate >= rnd;
	}

	// �ܡܡܡ� NPC �κ��� �÷��̾�� ���� ���� �ܡܡܡ�
	private boolean calcNpcPcHit() {
		_hitRate = _npc.getLevel() * 2;

		if (_npc instanceof L1PetInstance) {
			_hitRate += ((L1PetInstance) _npc).getHitByWeapon();
		}

		_hitRate += _npc.getHitup();
		_hitRate += (_npc.getLevel() - (_targetPc.getHighLevel() / 2));

		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(UNCANNY_DODGE)) {// ����
			_hitRate -= 4.5;
		}
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(MIRROR_IMAGE)) {//�̷��̹���
			_hitRate -= 5;
	    }
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(FEAR)) {
			_hitRate += 5;
		}
		_hitRate += (_targetPc.getAC().getAc()) / 5;

		if (_hitRate < 5)
			_hitRate = 5;// �ּ�ġ
		if (_hitRate > 95)
			_hitRate = 95;// �ִ�ġ

		// [���� ȸ�� �κ�]����.����.����.ź��
		if (_targetPc != null) {
			if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(ANTA_MAAN)
					|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(
							LIFE_MAAN)
					|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(
							SHAPE_MAAN)
					|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(
							BIRTH_MAAN)) {
				int chance = _random.nextInt(100) + 1;
				if (chance <= 3) {
					_hitRate = 0;
				}
			}
		}
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(ABSOLUTE_BARRIER)) {
			_hitRate = 0;
		}

		int rnd = _random.nextInt(100) + 1;
		// Ȱ����
		if (_npc.getNpcTemplate().get_ranged() >= 6
				&& _npc.getNpcTemplate().getBowActId() != 0
				&& _hitRate > rnd
				&& _npc.getLocation().getTileLineDistance(
						new Point(_targetX, _targetY)) >= 3) {
			return calcErEvasion();
		}
		return _hitRate >= rnd;
	}

	// �ܡܡܡ� NPC �κ��� NPC ���� ���� ���� �ܡܡܡ�
	private boolean calcNpcNpcHit() {
		int target_ac = 10 - _targetNpc.getAC().getAc();
		int attacker_lvl = _npc.getNpcTemplate().get_level();

		if (target_ac != 0) {
			_hitRate = (100 / target_ac * attacker_lvl); // �ǰ����� AC = ������ Lv
			// �� �� ������ 100%
		} else {
			_hitRate = 100 / 1 * attacker_lvl;
		}

		if (_npc instanceof L1PetInstance) { // ���� LV1���� �߰� ����+2
			_hitRate += _npc.getLevel() * 2;
			_hitRate += ((L1PetInstance) _npc).getHitByWeapon();
		}

		if (_hitRate < attacker_lvl) {
			_hitRate = attacker_lvl; // ���� ������=L����
		}
		if (_hitRate > 95) {
			_hitRate = 95; // �ְ� �������� 95%
		}
		if (_hitRate < 5) {
			_hitRate = 5; // ������ Lv�� 5 �̸����� ������ 5%
		}

		int rnd = _random.nextInt(100) + 1;
		return _hitRate >= rnd;
	}

	// �ܡܡܡ� ER�� ���� ȸ�� ���� �ܡܡܡ�
	private boolean calcErEvasion() {//���Ϻκ�
		int er = _targetPc.getEr();

		int rnd = _random.nextInt(110) + 1;
		return er < rnd;
	}
	public int getIceChain(L1PcInstance pc, L1Character target, int effect,
			int enchant) {//������ü�μҵ�
		int dmg = 0;
		int chance = _random.nextInt(100) + 1;
		if (8 >= chance) {
		_drainHp = _random.nextInt(5) + 27 + enchant;
		pc.sendPackets(new S_SkillSound(target.getId(), effect));
		Broadcaster.broadcastPacket(pc, new S_SkillSound(target.getId(),
				effect));
		}
		return dmg;
		} 
	/** �ܡܡܡ� �̺����� �̺�Ʈ���ܡܡܡ� */
	public int getEbHP(L1PcInstance pc, L1Character target, int effect,
			int enchant) { //  ��
		int dmg = 0;
		int in = (enchant * 2);
		int chance = _random.nextInt(100) + 1;
		if (chance <= (in) + 1) {
			_drainHp = _random.nextInt(5) + 25 + enchant; // �ǻ�
			pc.sendPackets(new S_SkillSound(target.getId(), effect));
			Broadcaster.broadcastPacket(pc, new S_SkillSound(target.getId(),
					effect));
		}
		return dmg;
	}

	public int getEbMP(L1PcInstance pc, L1Character target, int effect,
			int enchant) { // ũ�ο�Ȱ
		int dmg = 0;
		int in = (enchant * 2);
		int chance = _random.nextInt(100) + 1;
		if (chance <= (in) + 1) {
			_drainMana = _random.nextInt(5) + 5 + enchant; // ������
			pc.sendPackets(new S_SkillSound(target.getId(), effect));
			Broadcaster.broadcastPacket(pc, new S_SkillSound(target.getId(),
					effect));
		}
		return dmg;
	}
	public int getEbMP1(L1PcInstance pc, L1Character target, int effect,
			int enchant) { // ������
		int dmg = 0;
		int in = (enchant * 2);
		int chance = _random.nextInt(100) + 1;
		if (chance <= (in) + 1) {
			_drainMana = _random.nextInt(5) + 5 + enchant; // ������
			pc.sendPackets(new S_SkillSound(target.getId(), effect));
			Broadcaster.broadcastPacket(pc, new S_SkillSound(target.getId(),
					effect));
		}
		return dmg;
	}

	/* ���������������� ������ ���� ���������������� */

	public int calcDamage() {
		try {
			switch (_calcType) {
			case PC_PC:
				_damage = calcPcPcDamage();
				break;
			case PC_NPC:
				_damage = calcPcNpcDamage();
				break;
			case NPC_PC:
				_damage = calcNpcPcDamage();
				break;
			case NPC_NPC:
				_damage = calcNpcNpcDamage();
				break;
			default:
				break;
			}
		} catch (Exception e) {
		}
		return _damage;
	}

	// �ܡܡܡ� �÷��̾�κ��� �÷��̾�� ������ ���� �ܡܡܡ�
	public int calcPcPcDamage() {
		int weaponMaxDamage = _weaponSmall; //+ _weaponAddDmg;

		int weaponDamage = 0;

		int doubleChance = _random.nextInt(100) + 1;

		if (_weaponType1 == 11 && doubleChance <= _weaponDoubleDmgChance) { // ũ�ο�
			// ����
			weaponDamage = weaponMaxDamage + _weaponAddDmg;
			_attackType = 2;
		} else if (_weaponType == 0) { // �Ǽ�
			weaponDamage = 0;
		} else {
			weaponDamage = _random.nextInt(weaponMaxDamage) + _weaponAddDmg;
		}
		if (_pc.getSkillEffectTimerSet().hasSkillEffect(SOUL_OF_FLAME)) {
			if (_weaponType != 20 && _weaponType != 62) {
				weaponDamage = weaponMaxDamage + _weaponAddDmg+ 1;
			}
		}
		
		/**��Ǫ��ȣ**/
		  if (_target != _targetNpc) {
		   int chance5 = _random.nextInt(100) + 1;
		   int dmg2 = 0;
		   if(_targetPc.getInventory().checkEquipped(420104)|| //��Ǫ �Ϸ�
		         _targetPc.getInventory().checkEquipped(420105)|| //��Ǫ ������
		         _targetPc.getInventory().checkEquipped(420106)|| //��Ǫ �γ���
		         _targetPc.getInventory().checkEquipped(420107)){ //��Ǫ ����
		        if(chance5 <= 7){ //7% Ȯ��
		    if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.POLLUTE_WATER)) {
		           dmg2 += (50 + _random.nextInt(30)) / 2; //�÷�Ʈ���Ͱ������
		           }
		           if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.WATER_LIFE)) {
		           dmg2 += (50 + _random.nextInt(30)) * 2; //���Ͷ��������ι�
		           }
		    dmg2 += 50 + _random.nextInt(30); //ȸ���� = �⺻50ȸ��+����(1~30)
		    _targetPc.setCurrentHp(_targetPc.getCurrentHp() + dmg2);
		    _targetPc.sendPackets(new S_SystemMessage("��Ǫ������ ��ȣ�� �޾ҽ��ϴ�."));//��ȣ�۵��ø�Ʈ
		    _targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 2187));
		    _targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 2187));
		        }
		   }
		  }
		
		   /** �߶� ��ȣ**/
	     if (_pc != null) { 
	            if (_pc.getInventory().checkEquipped(20119) || 
	            		_pc.getInventory().checkEquipped(420112) || 
	              _pc.getInventory().checkEquipped(420113) ||
	              _pc.getInventory().checkEquipped(420114) ||
	              _pc.getInventory().checkEquipped(420115)) { // �߶� ����
	       int chance = _random.nextInt(100);
	       if (chance <= 6) { //Ȯ�� 10
	        _damage += 8; //�������߰� 5
	                 _pc.sendPackets(new S_SkillSound(_pc.getId(), 2185)); //����Ʈ
	                 _pc.broadcastPacket(new S_SkillSound(_pc.getId(), 2185));
	       }
	            }
	     }
	     /** �߶� ��ȣ**/
			/**���� ��ȣ**/
		    if (_targetPc != null) { 
		      int chance = _random.nextInt(100);
		            if (_targetPc.getInventory().checkEquipped(20108) || 
		            		_targetPc.getInventory().checkEquipped(420108) || 
		            		_targetPc.getInventory().checkEquipped(420109) ||
		            		_targetPc.getInventory().checkEquipped(420110) ||
		            		_targetPc.getInventory().checkEquipped(420111)) { //���� ����
		            if (chance <= 7){ //Ȯ�� 10
			            if (_weaponType == 20) {
			            	_damage += _random.nextInt(60) + 30;
			            	_pc.receiveDamage(_targetPc, _damage, _isHit);
			            	_targetPc.sendPackets(new S_SkillSound(_targetPc.getId() , 8120));//����Ʈ
			            	_targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 8120));
		            }
		          }
		     } 
		    }
		    /**���� ��ȣ**/
		// [���ȿ� ���� �߰�Ÿ��]ȭ��.����
		if (_pc != null) {
			if (_pc.getSkillEffectTimerSet().hasSkillEffect(VALA_MAAN)
					|| _pc.getSkillEffectTimerSet().hasSkillEffect(LIFE_MAAN)) {
				int chance = _random.nextInt(100);
				if (chance <= 20) {
					weaponDamage += 10;
				}
			}
		}
		int weaponTotalDamage = weaponDamage + _weaponEnchant;
		if (_weaponType == 54 && doubleChance <= _weaponDoubleDmgChance) { // �̵���
			// ����
			weaponTotalDamage *= 2.4;
			_attackType = 4;

		}
		
		
		if (_weaponEnchant == 7 && (_random.nextInt(100) + 1) <= Config.RATE_7_DMG_PER) { // ��þƮ 7�϶� �������� 5%
			weaponTotalDamage *= Config.RATE_7_DMG_RATE;
		}else if (_weaponEnchant == 8 && (_random.nextInt(100) + 1) <= Config.RATE_8_DMG_PER) { // ��þƮ 8�϶� �������� 10%
			weaponTotalDamage *= Config.RATE_8_DMG_RATE;
		}else if (_weaponEnchant == 9 && (_random.nextInt(100) + 1) <= Config.RATE_9_DMG_PER) { // ��þƮ 9�϶�
			weaponTotalDamage *= Config.RATE_9_DMG_RATE;
		}else if (_weaponEnchant == 10 && (_random.nextInt(100) + 1) <= Config.RATE_10_DMG_PER) { // ��þƮ 10�϶�
			weaponTotalDamage *= Config.RATE_10_DMG_RATE;
		}else if (_weaponEnchant == 11 && (_random.nextInt(100) + 1) <= Config.RATE_11_DMG_PER) { // ��þƮ 11�϶�
			weaponTotalDamage *= Config.RATE_11_DMG_RATE;
		}else if (_weaponEnchant == 12 && (_random.nextInt(100) + 1) <= Config.RATE_12_DMG_PER) { // ��þƮ 12�϶�
			weaponTotalDamage *= Config.RATE_12_DMG_RATE;
		}else if (_weaponEnchant == 13 && (_random.nextInt(100) + 1) <= Config.RATE_13_DMG_PER) { // ��þƮ 13�϶�
			weaponTotalDamage *= Config.RATE_13_DMG_RATE;
		}else if (_weaponEnchant == 14 && (_random.nextInt(100) + 1) <= Config.RATE_14_DMG_PER) { // ��þƮ 14�϶�
			weaponTotalDamage *= Config.RATE_14_DMG_RATE;
		}else if (_weaponEnchant == 15 && (_random.nextInt(100) + 1) <= Config.RATE_15_DMG_PER) { // ��þƮ 15�϶�
			weaponTotalDamage *= Config.RATE_15_DMG_RATE;
		}else if (_weaponEnchant == 16 && (_random.nextInt(100) + 1) <= Config.RATE_16_DMG_PER) { // ��þƮ 15�϶�
			weaponTotalDamage *= Config.RATE_16_DMG_RATE;
		}else if (_weaponEnchant == 17 && (_random.nextInt(100) + 1) <= Config.RATE_17_DMG_PER) { // ��þƮ 17�϶�
			weaponTotalDamage *= Config.RATE_17_DMG_RATE;
		}else if (_weaponEnchant == 18 && (_random.nextInt(100) + 1) <= Config.RATE_18_DMG_PER) { // ��þƮ 18�϶�
			weaponTotalDamage *= Config.RATE_18_DMG_RATE;
		}
		  
		  

		
		 try { 
			  weaponTotalDamage += WeaponAddDamage.getInstance().getWeaponAddDamage(_weaponId); 
			  WeaponAddDamage.getInstance().getWeaponAddDamage(_weaponId); 
			  } catch (Exception e) {
			  System.out.println("Weapon Add Damege Error"); 
			  }
		
		if (_pc.getSkillEffectTimerSet().hasSkillEffect(DOUBLE_BRAKE)
				&& (_weaponType == 54 || _weaponType1 == 11)) { // �̵��� ũ�ο� ����극��ũ��Ȯ��[2�赩]
			if ((_random.nextInt(100) + 1) <= 33) {
				weaponTotalDamage *= 2;
			}
		}

		double dmg = weaponTotalDamage + _statusDamage;
		if (_weaponType == 46) {//�ܰ˵���������
			weaponTotalDamage -= 0.3;
		}else if (_weaponType == 24) {
			weaponTotalDamage -= 0.2;
		}else if (_weaponId == 163) {//�ٶ�īũ�ο�
			weaponTotalDamage -= 0.8;
		}
		if (_weaponId == 7 || _weaponId == 35 || _weaponId == 48
				|| _weaponId == 73 || _weaponId == 105 || _weaponId == 224
				|| _weaponId == 120 || _weaponId == 147 || _weaponId == 156
				|| _weaponId == 174 || _weaponId == 175) {
			weaponTotalDamage -= 0.3;
			}
		if (_weaponType != 20 && _weaponType != 62) {
			dmg += _pc.getDmgup() + _pc.getDmgupByArmor();
		} else {
			dmg += _pc.getBowDmgup() + _pc.getBowDmgupByArmor()
					+ _pc.getBowDmgupByDoll() + _pc.getKnifeDmgupByDoll();
		}
		if (_weaponId == 47) { // ���� ��ȣ(�߰�)ħ���ǰ�
			int chance = _random.nextInt(100) + 1;
			if (chance < 4) { // Ȯ��
				_target.getSkillEffectTimerSet().killSkillEffectTimer(SILENCE); // ���Ϸ���
				_targetPc.getSkillEffectTimerSet().setSkillEffect(SILENCE,
						15000); // �ð�(5�ʰ�)
				_pc.sendPackets(new S_SkillSound(_targetId, 2177)); // �̹�����ȣ
				_pc.broadcastPacket(new S_SkillSound(_targetId, 2177)); // �̹�����ȣ
			}
		}
		if (_weaponType == 20) { // Ȱ
			if (_arrow != null) {
				int add_dmg = _arrow.getItem().getDmgSmall();
				if (add_dmg == 0) {
					add_dmg = 1;
				}
				dmg += _random.nextInt(add_dmg) + 1;
			} else if (_weaponId == 190
					|| (_weaponId >= 11011 && _weaponId <= 11013)) {
				// �������� Ȱ
				dmg += _random.nextInt(15) + 1;
			}
		} else if (_weaponType == 62) { // �� ���� ��
			int add_dmg = _sting.getItem().getDmgSmall();
			if (add_dmg == 0) {
				add_dmg = 1;
			}
			dmg = dmg + _random.nextInt(add_dmg) + 1;
		}


		/** �� Ŭ������ �߰� ������ **/
		dmg = calcBuffDamage(dmg);
		// 70������ 1���� �߰� ������ 1��..�������� �ٲٰ������ �ڿ����� 2�� �ٲټ���
		dmg += Math.max(0, _pc.getLevel() - 70) * 1; // ���絥�����߰�1
		
		if (_calcType == PC_PC) {
		if(_pc.isCrown()) { // ������Ÿ
			//dmg += 9;
			dmg += Config.PRINCE_ADD_DAMAGEPC;
		}else if(_pc.isKnight()) { //��� ��Ÿ
			//dmg += 12;	
			dmg += Config.KNIGHT_ADD_DAMAGEPC;
		} else if (_pc.isElf()) { 
			//dmg += 5;
			dmg += Config.ELF_ADD_DAMAGEPC;
		} else if (_pc.isDarkelf()) {
			//dmg += 5;
			dmg += Config.DARKELF_ADD_DAMAGEPC;
		} else if (_pc.isWizard()) { 
			//dmg += 4;
			dmg += Config.WIZARD_ADD_DAMAGEPC;
		} else if (_pc.isDragonknight()) {
			//dmg += 4;
			dmg += Config.DRAGONKNIGHT_ADD_DAMAGEPC;
		}else if(_pc.isIllusionist()) { //ȯ����Ÿ
			//dmg += 7;
			dmg += Config.BLACKWIZARD_ADD_DAMAGEPC;
		}
		}
		// 70������ 1���� �߰� ������ 1��..�������� �ٲٰ������ �ڿ����� 2�� �ٲټ���
		/** �� Ŭ������ �߰� ������ **/
		if (_weaponType == 0) { // �Ǽ�
			dmg = (_random.nextInt(5) + 4) / 4;
		}
		int Rdmg7 = _random.nextInt(3) + 3; // �ּ�5 �ִ�5 .5~10������Ÿ�������ٴ¸�..
		int Rdmg8 = _random.nextInt(4) + 4; // �� ����Ư���� �°� ��Ÿ�κ��� �������ֽ� �� ��
		int Rdmg9 = _random.nextInt(6) + 6;
		int Rdmg10 = _random.nextInt(9) + 9;
		int Rdmg11 = _random.nextInt(12) + 12;
		int Rdmg12 = _random.nextInt(15) + 15;
		int Rdmg13 = _random.nextInt(18) + 18;
		int Rdmg14 = _random.nextInt(21) + 21;
		int Rdmg15 = _random.nextInt(28) + 28;
		if (_weaponEnchant == 7) {
			weaponTotalDamage += Rdmg7;
		}else if (_weaponEnchant == 8) {
			weaponTotalDamage += Rdmg8;
		}else if (_weaponEnchant == 9) {
			weaponTotalDamage += Rdmg9;
		}else if (_weaponEnchant == 10) {
			weaponTotalDamage += Rdmg10;
		}else if (_weaponEnchant == 11) {
			weaponTotalDamage += Rdmg11;
		}else if (_weaponEnchant == 12) {
			weaponTotalDamage += Rdmg12;
		}else if (_weaponEnchant == 13) {
			weaponTotalDamage += Rdmg13;
		}else if (_weaponEnchant == 14) {
			weaponTotalDamage += Rdmg14;
		}else if (_weaponEnchant >= 15) {
			weaponTotalDamage += Rdmg15;
		}
		 if ( _weaponId == 62 || _weaponId == 100062 ||_weaponId ==4500081
				|| _weaponId == 294 || _weaponId == 294000 || _weaponId == 294000 || _weaponId == 415010|| _weaponId == 58
				|| _weaponId == 412001 || _weaponId == 412005 || _weaponId == 58000
				|| _weaponId == 59 || _weaponId == 450006 || _weaponId == 190 || _weaponId == 45000600
				|| _weaponId == 189 || _weaponId == 410003
				|| _weaponId == 410004 || _weaponId == 100189|| _weaponId ==
					 4500101||_weaponId == 450014|| _weaponId == 421) {
			if (_weaponEnchant >= 0) {weaponTotalDamage += Rdmg8;
			}
		} // ��� ��� ���˺κ�
		 
		 if (_weaponId == 62 || _weaponId == 100062 || _weaponId == 450014) { // ������հ�
				weaponTotalDamage += Rdmg7;
			}
		 
		 if (_weaponId == 61 || _weaponId == 134 || _weaponId == 12 || _weaponId == 61000 || _weaponId == 86000 || _weaponId == 134000
				|| _weaponId == 86) { // ����
			// ������
			// �ٶ��ܰ��϶�.
			if (_weaponEnchant >= 0) weaponTotalDamage += Rdmg15 * (_weaponEnchant + 1);
		}
		if (_weaponId == 134 ||_weaponId == 134000 ) { // ������
			weaponTotalDamage += 5;
		}else if (_weaponId == 450009||_weaponId == 450010||_weaponId == 450008||_weaponId == 100084) { // ������ü�μҵ�
			weaponTotalDamage += 3;
		}else if (_weaponId == 450013) { // ����Ű��ũ
			weaponTotalDamage += 5;
		}else if (_weaponId == 4500101||_weaponId == 4500091||_weaponId == 164||_weaponId == 100164||_weaponId == 100157) { // �Ź������
			weaponTotalDamage += 2;
		}else if (_weaponId == 86 || _weaponId == 86000 ) { // �����׸����̵���
			weaponTotalDamage += 10;
		}else if (_weaponId == 12) { // �ٴ�
			weaponTotalDamage += 8;
		}else if (_weaponId == 189||_weaponId == 190||_weaponId == 100189) { // ��ձ�
			weaponTotalDamage += 4;
		}else if (_weaponId == 59) { // ����Ʈ�ߵ��ǰ�
			weaponTotalDamage += 8;
		}else if (_weaponId == 450014) { // ����Ʈ�ߵ��Ǿ�հ�
			weaponTotalDamage += 9;
			
		}
		if (_weaponId == 450008 || _weaponId == 450010
				|| _weaponId == 450009 // <<�����ȣ
				|| _weaponId == 450011 || _weaponId == 450012
				|| _weaponId == 450013
			 || _weaponId == 4500081 || _weaponId == 4500091 || _weaponId ==
			 4500101){
			if (_weaponEnchant >= 0) weaponTotalDamage += Rdmg7 * (_weaponEnchant + 1);
		}
		if (_weaponType1 == 17||_weaponType1 == 19) {
			dmg = WeaponSkill.getKiringkuDamage(_pc, _target);
			try {
				dmg += WeaponAddDamage.getInstance().getWeaponAddDamage(_weaponId);
			} catch (Exception e) {
				System.out.println("Weapon Add Damege Error");
			}
		}
		if (!_pc.FouSlayer) {
			switch (_weaponId) {

			case 2://�ǿ��Ǵܰ�
			case 200002:
				dmg += WeaponSkill.getDiceDaggerDamage(_pc, _targetPc, weapon);
				break;
			case 13:
			case 44:
				WeaponSkill.getPoisonSword(_pc, _targetPc);
				break;
			case 47:
				WeaponSkill.getSilenceSword(_pc, _targetPc);
				break;
			case 54:
				dmg += WeaponSkill.getKurtSwordDamage(_pc, _targetPc);
				break;
			case 134:
			case 134000:	
				dmg += WeaponSkill.getKurtSwordDamage(_pc, _targetPc);
				break;	
				
			case 58:
			case 58000:
				dmg += WeaponSkill.getDeathKnightSwordDamage(_pc, _targetPc);
				break;
			case 76:
				dmg += WeaponSkill.getRondeDamage(_pc, _targetPc);
				break;
			case 121:
				dmg += WeaponSkill.getIceQueenStaffDamage(_pc, _target, _weaponEnchant);
				break;
			case 124:
				dmg += WeaponSkill.getBaphometStaffDamage(_pc, _target);
				break;
			case 126:
			case 127:
				calcStaffOfMana();
				break;
			case 421: 
				WeaponSkill.getMindBreak(_pc, _target); 
			break;

			case 420://������ü�μҵ�
				dmg += WeaponSkill.getChainSwordDamage(_pc, _target);
				dmg += getIceChain(_pc, _target, 3685, _weaponEnchant); 
				break;
			case 4500091: //�Ź�������8981
				dmg += getEbMP1(_pc, _target, 8981, _weaponEnchant);
				break;
			case 4500111:  //�Ź����
				dmg += getEbMP(_pc, _target, 8981, _weaponEnchant);
				break;
			case 450011: //����������
			case 450013: //����Ű��ũ
				dmg += getEbMP1(_pc, _target, 8152, _weaponEnchant);
				break;
			case 450012: //����Ȱ
				dmg += getEbMP(_pc, _target, 8152, _weaponEnchant);
				break;
			case 342://�ҷ����̺�Ʈ2011
				WeaponSkill.gettjdrhdghkf(_pc, _targetPc);
			break;
			case 341://�ҷ����̺�Ʈ2011
				WeaponSkill.gettjdrhdghkf(_pc, _targetPc);
				break;
			case 203:
				dmg += WeaponSkill.getBarlogSwordDamage(_pc, _target);
				break;
			case 204:
			case 100204:
				WeaponSkill.giveFettersEffect(_pc, _targetPc);
				break;
			case 205:
				double monValue = WeaponSkill.getMoonBowDamage(_pc, _target,
						_weaponEnchant);
				if (monValue > 0)
					dmg += (int) (monValue * 1.0);
				else
					dmg *= 0.7;
				break;
			case 256:
			case 340://�ҷ�����հ�
				dmg += WeaponSkill.getEffectSwordDamage(_pc, _target, 2750);
				break;
			case 410000://ü�μҵ�
			case 410001:
			case 450004:
			case 411030:
			case 411031:
			case 411032:
            dmg += WeaponSkill.getChainSwordDamage(_pc, _target);
				break;
			case 45735://���弭Ŀ
			case 412001://�ĸ��Ǵ��
			case 12://�ٴ�
				calcDrainOfHp(dmg);
				break;
			case 4500081: //�Ź����
			case 4500101: //�Ź�ũ�ο�
				dmg += getEbHP(_pc, _target, 8981, _weaponEnchant);
				break;
			case 450009: //����ü�μҵ�
				dmg += WeaponSkill.getChainSwordDamage(_pc, _target);
				dmg += getEbHP(_pc, _target, 8150, _weaponEnchant);
				break;
			case 450008: //������
			case 450010: //����ũ�ο�
				dmg += getEbHP(_pc, _target, 8150, _weaponEnchant);
				break;
			case 412004://Ȥ����â
				dmg += WeaponSkill.getȤ��âDamage(_pc, _target,_weaponEnchant);
				break;
			case 412005://��ǳ����
				dmg += WeaponSkill.get��ǳ����Damage(_pc, _target,_weaponEnchant);
				break;
			case 423://����Ű��ũ 
			    dmg += WeaponSkill.get����Damage(_pc, _target); 
			    break;
			case 412000://���Ű�
				dmg += WeaponSkill.get���Ű�Damage(_pc, _target, _weaponEnchant);
				break;
			case 191:
			case 191000://��õ
				dmg += WeaponSkill.get��õ��ȰDamage(_pc, _target, _weaponEnchant);
				break;
			case 412002://�����Ǵܰ�
				calcDrainOfMana();
				break;
			case 46172://�ı����̵���
			case 46173:
				dmg += WeaponSkill.get�ı����̵���Damage(_pc, _target, _weaponEnchant);
				break;	
			case 413101:
			case 413102:
			case 413104:
			case 413105:
				WeaponSkill.getDiseaseWeapon(_pc, _target, 413101);
				break;
			case 413106:
				dmg += WeaponSkill.halloweenCus(_pc, _target);
				break;
			case 415010:
			case 415011:
			case 415012:
			case 415013:
			case 4150101:
			case 4150111:
			case 4150121:
			case 4150131:
				dmg += WeaponSkill.getChaserDamage(_pc, _target, 6985);
				break;
			case 415015:
			case 415016:
				dmg += WeaponSkill.getChaserDamage(_pc, _target, 7179);
				break;
			case 413103:
				calcStaffOfMana();
				WeaponSkill.getDiseaseWeapon(_pc, _target, 413101);
				break;
			default:
				break;
			}
		}

		for (L1DollInstance doll : _pc.getDollList().values()) {
			if (doll == null)
				continue;
			if (_weaponType != 20 && _weaponType != 62) {
				dmg += doll.getDamageByDoll();
			}
			doll.attackPoisonDamage(_pc, _targetPc);
		}

		if (_pc.getSkillEffectTimerSet().hasSkillEffect(BURNING_SLASH)) {
			dmg += 10;
			_pc.sendPackets(new S_SkillSound(_targetPc.getId(), 6591));
			Broadcaster.broadcastPacket(_pc, new S_SkillSound(
					_targetPc.getId(), 6591));
			_pc.getSkillEffectTimerSet().killSkillEffectTimer(BURNING_SLASH);
		}

		dmg -= _targetPc.getDamageReductionByArmor(); // ���� �ⱸ�� ���� ������ �氨

		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(SPECIAL_COOKING)) { // ����ȿ丮��
			// ����
			// ������
			// �氨
			dmg -= 5;
		}
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SPECIAL_COOKING2)) { // ����ȿ丮��
			// ����
			// ������
			// �氨
			dmg -= 2;
		}
		// � ����
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(
				L1SkillId.STATUS_LUCK_A)) {
			dmg -= 3;
		}
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(
				L1SkillId.STATUS_LUCK_B)) {
			dmg -= 2;
		} // � ����
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(REDUCTION_ARMOR)) {
			int targetPcLvl = _targetPc.getLevel();
			if (targetPcLvl < 50) {
				targetPcLvl = 50;
			}
			dmg -= (targetPcLvl - 50) / 5 + 1;
		}
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(IMMUNE_TO_HARM)) {
			dmg /= 1.4;
		}
		if (_targetPc.getInventory().checkEquipped(420012)
				||_targetPc.getInventory().checkEquipped(500042)){//���.����
			dmg -= 1;
		}
		if (_targetPc.getInventory().checkEquipped(20117)){//��������
			dmg -= 2;
		}
		if (_targetPc.getInventory().checkEquipped(500040)){//�ݿ����ǹ���
			dmg -= 3;
		}
	    if (_target !=_targetNpc) { 
		      int chance = _random.nextInt(100);
		            if (_targetPc.getInventory().checkEquipped(500040)) { //�ݿ����ǹ���
		      if (chance <= 2){ //Ȯ�� 2
		        dmg /= 2; //������ ���� 2
		        _targetPc.sendPackets(new S_SkillSound(_targetPc.getId() , 6320)); //����Ʈ
			       _targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 6320)); 
		      }
		          }
		        }
		 /**��Ÿ ��ȣ**/
	    if (_target !=_targetNpc) { 
	      int chance = _random.nextInt(100);
	            if (_targetPc.getInventory().checkEquipped(20130) || 
	            		_targetPc.getInventory().checkEquipped(420100) || 
	              _targetPc.getInventory().checkEquipped(420101) ||
	              _targetPc.getInventory().checkEquipped(420102) ||
	              _targetPc.getInventory().checkEquipped(420103)) { //��Ÿ  ����
	      if (chance <= 5){ //Ȯ�� 10
	        dmg /= 3; //������ ���� 3
	   //����    _targetPc.sendPackets(new S_SkillSound(_targetPc.getId() , 2183)); //����Ʈ
	   //����   _targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 2183)); 
	      }
	          }
	        }
	   /**��Ÿ ��ȣ**/
 		if(_calcType == PC_PC){
			if(_targetPc.getLevel() < Config.MAX_LEVEL || _pc.getLevel() < Config.MAX_LEVEL){ //�űԺ�ȣ
					dmg = 0;
           } 
         }
 		if(_calcType == PC_PC){
		if(_targetPc.getMapId() == 777 ||_targetPc.getMapId() == 778||_targetPc.getMapId() == 779 ||_targetPc.getMapId() == 56){ //��������������̷κ���
			dmg = 0;
			}
 		}
 		if(_calcType == PC_PC){
		if(_pc.getMapId() == 777 ||_pc.getMapId() == 778||_pc.getMapId() == 779 ||_pc.getMapId() == 56){ //��������������̷κ���
			dmg = 0;
			}
 		}
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(IllUSION_AVATAR)) {
			dmg += dmg / 5;
		}
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(ARMOR_BRAKE)){//�ƸӺ극��ũ
			if (_weaponType != 20 || _weaponType != 62) {
				dmg *= 1.45;
			}
		}
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(DRAGON_SKIN)) {
			dmg -= 2;
		}
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(PATIENCE)) {
			dmg -= 2;
		}
		if (_pc.getSkillEffectTimerSet().hasSkillEffect(COUNTER_BARRIER)) {//ī����ù����������������
			  if(_weaponId == 62 || _weaponId == 100062)
				  dmg *= 1.1;
		}
	//	if (_pc.getSkillEffectTimerSet().hasSkillEffect(SOUL_OF_FLAME)) {// ����������
			//dmg *= 1.1;
	//	}
		/** (����) ������Ʈ ������ ������Ʈ�� ����ġ�� ���� ��Ÿ ���� * */
		if (_pc.getLawful() <= -10000 && _pc.getLawful() >= -19999) {
			dmg += 1;
		} else if (_pc.getLawful() <= -20000 && _pc.getLawful() >= -29999) {
			dmg += 2;
		} else if (_pc.getLawful() <= -30000 && _pc.getLawful() >= -32767) {
			dmg += 4;
		}

		if (dmg <= 0) {
			_isHit = false;
			_drainHp = 0;
		}

		if (_pc.FouSlayer)
			dmg *= 0.8;

		return (int) dmg;
	}

	private int calcAttrEnchantDmg() {
		int dmg = 0;
		/** �Ӽ���æƮ �߰� Ÿ��ġ */
		switch (_weaponAttrEnchantLevel) {
		case 1:
		case 4:
		case 7:
		case 10:
			dmg = 1;
			break;
		case 2:
		case 5:
		case 8:
		case 11:
			dmg = 3;
			break;
		case 3:
		case 6:
		case 9:
		case 12:
			dmg = 5;
			break;
		default:
			dmg = 0;
			break;
		}
		return dmg;
	}

	// �ܡܡܡ� �÷��̾�κ��� NPC ���� ������ ���� �ܡܡܡ�
	private int calcPcNpcDamage() {
		if (_targetNpc == null || _pc == null) {
			_isHit = false;
			_drainHp = 0;
			return 0;
		}
		int weaponMaxDamage = 0;

		int doubleChance = _random.nextInt(100) + 1;

		if (_targetNpc.getNpcTemplate().get_size().equalsIgnoreCase("small")
				&& _weaponSmall > 0) {
			weaponMaxDamage = _weaponSmall;
		} else if (_targetNpc.getNpcTemplate().get_size().equalsIgnoreCase(
				"large")
				&& _weaponLarge > 0) {
			weaponMaxDamage = _weaponLarge;
		}

		//weaponMaxDamage += _weaponAddDmg;

		int weaponDamage = 0;

		if (_weaponType1 == 11 && doubleChance <= _weaponDoubleDmgChance) { // ����
			// ��Ʈ
			weaponDamage = weaponMaxDamage + _weaponAddDmg;
			_attackType = 2;

		} else if (_weaponType == 0) { // �Ǽ�
			weaponDamage = 0;
		} else {
			weaponDamage = _random.nextInt(weaponMaxDamage) + _weaponAddDmg;
		}

		if (_pc.getSkillEffectTimerSet().hasSkillEffect(SOUL_OF_FLAME)) {
			if (_weaponType != 20 && _weaponType != 62) {
				weaponDamage = weaponMaxDamage  + _weaponAddDmg + 1;
			}
		}
		   /** �߶� ��ȣ**/
	     if (_pc != null) { 
	            if (_pc.getInventory().checkEquipped(20119) || 
	            		_pc.getInventory().checkEquipped(420112) || 
	              _pc.getInventory().checkEquipped(420113) ||
	              _pc.getInventory().checkEquipped(420114) ||
	              _pc.getInventory().checkEquipped(420115)) { // �߶� ����
	       int chance = _random.nextInt(100);
	       if (chance <= 6) { //Ȯ�� 10
	        _damage += 8; //�������߰� 5
	                 _pc.sendPackets(new S_SkillSound(_pc.getId(), 2185)); //����Ʈ
	                 _pc.broadcastPacket(new S_SkillSound(_pc.getId(), 2185));
	       }
	            }
	     }
	     /** �߶� ��ȣ**/
		// [���ȿ� ���� �߰�Ÿ��]ȭ��.����
		if (_pc != null) {
			if (_pc.getSkillEffectTimerSet().hasSkillEffect(VALA_MAAN)
					|| _pc.getSkillEffectTimerSet().hasSkillEffect(LIFE_MAAN)) {
				int chance = _random.nextInt(100);
				if (chance <= 20) { //
					weaponDamage += 10;
				}
			}
		}
		int weaponTotalDamage = weaponDamage + _weaponEnchant;

		weaponTotalDamage += calcMaterialBlessDmg(); // ���ູ ������ ���ʽ�

		if (_weaponType == 54 && doubleChance <= _weaponDoubleDmgChance) { // ����
			// ��Ʈ
			weaponTotalDamage *= 2.5;
			_attackType = 4;

		}
		

		if (_weaponEnchant == 7 && (_random.nextInt(100) + 1) <= Config.RATE_7_DMG_PER) { // ��þƮ 7�϶� �������� 5%
			weaponTotalDamage *= Config.RATE_7_DMG_RATE;
		}else if (_weaponEnchant == 8 && (_random.nextInt(100) + 1) <= Config.RATE_8_DMG_PER) { // ��þƮ 8�϶� �������� 10%
			weaponTotalDamage *= Config.RATE_8_DMG_RATE;
		}else if (_weaponEnchant == 9 && (_random.nextInt(100) + 1) <= Config.RATE_9_DMG_PER) { // ��þƮ 9�϶�
			weaponTotalDamage *= Config.RATE_9_DMG_RATE;
		}else if (_weaponEnchant == 10 && (_random.nextInt(100) + 1) <= Config.RATE_10_DMG_PER) { // ��þƮ 10�϶�
			weaponTotalDamage *= Config.RATE_10_DMG_RATE;
		}else if (_weaponEnchant == 11 && (_random.nextInt(100) + 1) <= Config.RATE_11_DMG_PER) { // ��þƮ 11�϶�
			weaponTotalDamage *= Config.RATE_11_DMG_RATE;
		}else if (_weaponEnchant == 12 && (_random.nextInt(100) + 1) <= Config.RATE_12_DMG_PER) { // ��þƮ 12�϶�
			weaponTotalDamage *= Config.RATE_12_DMG_RATE;
		}else if (_weaponEnchant == 13 && (_random.nextInt(100) + 1) <= Config.RATE_13_DMG_PER) { // ��þƮ 13�϶�
			weaponTotalDamage *= Config.RATE_13_DMG_RATE;
		}else if (_weaponEnchant == 14 && (_random.nextInt(100) + 1) <= Config.RATE_14_DMG_PER) { // ��þƮ 14�϶�
			weaponTotalDamage *= Config.RATE_14_DMG_RATE;
		}else if (_weaponEnchant == 15 && (_random.nextInt(100) + 1) <= Config.RATE_15_DMG_PER) { // ��þƮ 15�϶�
			weaponTotalDamage *= Config.RATE_15_DMG_RATE;
		}else if (_weaponEnchant == 16 && (_random.nextInt(100) + 1) <= Config.RATE_16_DMG_PER) { // ��þƮ 15�϶�
			weaponTotalDamage *= Config.RATE_16_DMG_RATE;
		}else if (_weaponEnchant == 17 && (_random.nextInt(100) + 1) <= Config.RATE_17_DMG_PER) { // ��þƮ 17�϶�
			weaponTotalDamage *= Config.RATE_17_DMG_RATE;
		}else if (_weaponEnchant == 18 && (_random.nextInt(100) + 1) <= Config.RATE_18_DMG_PER) { // ��þƮ 18�϶�
			weaponTotalDamage *= Config.RATE_18_DMG_RATE;
		}
		
		try {
			// System.out.println("������ : " + weaponTotalDamage);
			weaponTotalDamage += WeaponAddDamage.getInstance()
					.getWeaponAddDamage(_weaponId);
			// System.out.println("������ : " +
			// WeaponAddDamage.getInstance().getWeaponAddDamage(_weaponId));
			// System.out.println("������ : " + weaponTotalDamage);

		} catch (Exception e) {
			System.out.println("Weapon Add Damege Error");
		}

		weaponTotalDamage += calcAttrEnchantDmg();

		if (_pc.getSkillEffectTimerSet().hasSkillEffect(DOUBLE_BRAKE) // ����극��ũ
																		// [2�赩]
				&& (_weaponType == 54 || _weaponType1 == 11)) {
			if ((_random.nextInt(100) + 1) <= 33) {
				weaponTotalDamage *= 2;
			}
		}

		double dmg = weaponTotalDamage + _statusDamage;
		if (_weaponType == 24) {
			weaponTotalDamage -= 0.2;
}
		 if (_weaponId == 7 || _weaponId == 35 || _weaponId == 48
				|| _weaponId == 73 || _weaponId == 105 || _weaponId == 224
				|| _weaponId == 120 || _weaponId == 147 || _weaponId == 156
				|| _weaponId == 174 || _weaponId == 175) {
			weaponTotalDamage -= 0.3;
		}
		if (_weaponType != 20 && _weaponType != 62) {
			dmg += _pc.getDmgup() + _pc.getDmgupByArmor();
		} else {
			dmg += _pc.getBowDmgup() + _pc.getBowDmgupByArmor()
					+ _pc.getBowDmgupByDoll() + _pc.getKnifeDmgupByDoll();
		}


		int Rdmg7 = _random.nextInt(3) + 3; // �ּ�5 �ִ�5 .5~10������Ÿ�������ٴ¸�..
		int Rdmg8 = _random.nextInt(4) + 4; // �� ����Ư���� �°� ��Ÿ�κ��� �������ֽ� �� ��
		int Rdmg9 = _random.nextInt(6) + 6;
		int Rdmg10 = _random.nextInt(9) + 9;
		int Rdmg11 = _random.nextInt(12) + 12;
		int Rdmg12 = _random.nextInt(15) + 15;
		int Rdmg13 = _random.nextInt(18) + 18;
		int Rdmg14 = _random.nextInt(21) + 21;
		int Rdmg15 = _random.nextInt(24) + 24;
		if (_weaponEnchant == 7) {
			weaponTotalDamage += Rdmg7;
		}else if (_weaponEnchant == 8) {
			weaponTotalDamage += Rdmg8;
		}else if (_weaponEnchant == 9) {
			weaponTotalDamage += Rdmg9;
		}else if (_weaponEnchant == 10) {
			weaponTotalDamage += Rdmg10;
		}else if (_weaponEnchant == 11) {
			weaponTotalDamage += Rdmg11;
		}else if (_weaponEnchant == 12) {
			weaponTotalDamage += Rdmg12;
		}else if (_weaponEnchant == 13) {
			weaponTotalDamage += Rdmg13;
		}else if (_weaponEnchant == 14) {
			weaponTotalDamage += Rdmg14;
		}else if (_weaponEnchant >= 15) {
			weaponTotalDamage += Rdmg15;
	    } if (_weaponId == 294 || _weaponId == 294000) { // �����ǰ�
			weaponTotalDamage += Rdmg7;
		}
	    
		if (_weaponId == 62 ||_weaponId ==100062 ||_weaponId == 450014) { // ������հ�
			weaponTotalDamage += Rdmg7;
		} 
		if (_weaponId == 61 || _weaponId == 134 || _weaponId == 12 || _weaponId == 61000 || _weaponId == 86000 || _weaponId == 134000
				|| _weaponId == 86) { // ����
			// ������
			// �ٶ��ܰ��϶�.
			if (_weaponEnchant >= 0) weaponTotalDamage += Rdmg15 * (_weaponEnchant + 1);
		}
		if (_weaponId == 134 ||_weaponId == 134000 ) { // ������
			weaponTotalDamage += 5;
		}else if (_weaponId == 450009||_weaponId == 450010||_weaponId == 450008||_weaponId == 100084) { // ������ü�μҵ�
			weaponTotalDamage += 3;
		}else if (_weaponId == 450013) { // ����Ű��ũ
			weaponTotalDamage += 5;
		}else if (_weaponId == 4500101||_weaponId == 4500091||_weaponId == 164||_weaponId == 100164||_weaponId == 100157) { // �Ź������
			weaponTotalDamage += 2;
		}else if (_weaponId == 86 ||_weaponId == 86000 ) { // �����׸����̵���
			weaponTotalDamage += 10;
		}else if (_weaponId == 12) { // �ٴ�
			weaponTotalDamage += 8;
		}else if (_weaponId == 189||_weaponId == 190||_weaponId == 100189) { // ��ձ�
			weaponTotalDamage += 4;
		}else if (_weaponId == 59) { // ����Ʈ�ߵ��ǰ�
			weaponTotalDamage += 8;
		}else if (_weaponId == 450014) { // ����Ʈ�ߵ��Ǿ�հ�
			weaponTotalDamage += 8;
		}
		


		
		if (_weaponId == 450008 || _weaponId == 450010
				|| _weaponId == 450009 // <<�����ȣ
				|| _weaponId == 450011 || _weaponId == 450012
				|| _weaponId == 450013
			 || _weaponId == 4500081 || _weaponId == 4500091 || _weaponId ==
			 4500101){
			if (_weaponEnchant >= 0) weaponTotalDamage += Rdmg7 * (_weaponEnchant + 1);
		}
		
		
		
		if (_weaponType == 20) { // Ȱ
			if (_arrow != null) {
				int add_dmg = 0;
				if (_targetNpc.getNpcTemplate().get_size().equalsIgnoreCase(
						"large")) {
					add_dmg = _arrow.getItem().getDmgLarge();
				} else {
					add_dmg = _arrow.getItem().getDmgSmall();
				}
				if (add_dmg == 0) {
					add_dmg = 1;
				}
				if (_targetNpc.getNpcTemplate().is_hard()) {
					add_dmg /= 2;
				}
				dmg = dmg + _random.nextInt(add_dmg) + 1;
			} else if (_weaponId == 190
					|| (_weaponId >= 11011 && _weaponId <= 11013)) {
				// / �������� Ȱ
				dmg = dmg + _random.nextInt(15) + 1;
			}
		} else if (_weaponType == 62) { // �� ���� ��
			int add_dmg = 0;
			if (_targetNpc.getNpcTemplate().get_size()
					.equalsIgnoreCase("large")) {
				add_dmg = _sting.getItem().getDmgLarge();
			} else {
				add_dmg = _sting.getItem().getDmgSmall();
			}
			if (add_dmg == 0) {
				add_dmg = 1;
			}
			dmg = dmg + _random.nextInt(add_dmg) + 1;
		}

		dmg = calcBuffDamage(dmg); 
		/** AC�� ���� ������ ���� ������ **/
		//  dmg -= dmg * (calcNpcDefense() * 0.0035);
		  /** AC�� ���� ������ ���� ������ **/  
		// 70������ 1���� �߰� ������ 2��..�������� �ٲٰ������ �ڿ����� 2�� �ٲټ���
		dmg += Math.max(0, _pc.getLevel() - 70) * 1; // ���絥�����߰�2

		// 70������ 1���� �߰� ������ 2��..�������� �ٲٰ������ �ڿ����� 2�� �ٲټ���

		if (_weaponType == 0) { // �Ǽ�
			dmg = (_random.nextInt(5) + 4) / 4;
		}
		if (_weaponType1 == 17||_weaponType1 == 19) { // Ű��ũ
			dmg = WeaponSkill.getKiringkuDamage(_pc, _target);
		
			try {
				dmg += WeaponAddDamage.getInstance().getWeaponAddDamage(_weaponId);
			//	System.out.println("������ : " + weaponTotalDamage);
			} catch (Exception e) {
				System.out.println("Weapon Add Damege Error");
			}
		
		}
		
		
		
		switch (_weaponId) {
		   case 2://�ǿ��Ǵܰ�
		   case 200002: dmg += WeaponSkill.getDiceDaggerDamage(_pc, _targetPc, weapon); break;
		case 13:
		case 44:
			WeaponSkill.getPoisonSword(_pc, _target);
			break;
		case 47:
			WeaponSkill.getSilenceSword(_pc, _target);
			break;
		case 54:
			dmg += WeaponSkill.getKurtSwordDamage(_pc, _target);
			break;
		case 134:
		case 134000:
			dmg += WeaponSkill.getKurtSwordDamage(_pc, _target);
			break;
		case 58:
		case 58000:
			dmg += WeaponSkill.getDeathKnightSwordDamage(_pc, _target);
			break;
		case 76:
			dmg += WeaponSkill.getRondeDamage(_pc, _target);
			break;
		case 121:
			dmg += WeaponSkill.getIceQueenStaffDamage(_pc, _target, _weaponEnchant);
			break;
		case 124:
			dmg += WeaponSkill.getBaphometStaffDamage(_pc, _target);
			break;
		case 126:
		case 127:
			calcStaffOfMana();
			break;
		case 421: WeaponSkill.getMindBreak(_pc, _target); break;

		case 420: //������ü�μҵ�
			dmg += WeaponSkill.getChainSwordDamage(_pc, _target);
			dmg += getIceChain(_pc, _target, 3685, _weaponEnchant); 
			break;
		case 4500091: //�Ź�������
			dmg += getEbMP1(_pc, _target, 8981, _weaponEnchant);
			break;
		case 4500111:  //�Ź����
			dmg += getEbMP(_pc, _target, 8981, _weaponEnchant);
			break;
		case 450011: //����������
		case 450013: //����Ű��ũ
			dmg += getEbMP1(_pc, _target, 8152, _weaponEnchant);
			break;
		case 450012: //����Ȱ
			dmg += getEbMP(_pc, _target, 8152, _weaponEnchant);
			break;
		case 342://�ҷ����̺�Ʈ2011
					WeaponSkill.gettjdrhdghkf(_pc, _target);
			break;
		case 341://�ҷ����̺�Ʈ2011
			WeaponSkill.gettjdrhdghkf(_pc, _target);
			break;
		case 203:
			dmg += WeaponSkill.getBarlogSwordDamage(_pc, _target);
			break;
		case 204:
		case 100204:
			WeaponSkill.giveFettersEffect(_pc, _target);
			break;
		case 205:
			double monValue = WeaponSkill.getMoonBowDamage(_pc, _target,
					_weaponEnchant);
			if (monValue > 0)
				dmg += monValue;
			else
				dmg *=0.8;
			break;
		case 256:
		case 340://�ҷ�����հ�
			dmg += WeaponSkill.getEffectSwordDamage(_pc, _target, 2750);
			break;
		case 410000://ü�μҵ�
		case 410001:
		case 450004:
		case 411030:
		case 411031:
		case 411032:
			dmg += WeaponSkill.getChainSwordDamage(_pc, _target);
			break;
		case 45735://���弭Ŀ
		case 412001://�ĸ��Ǵ��
		case 12://�ٴ�
			calcDrainOfHp(dmg);
			break;
		case 4500081: //�Ź����
		case 4500101://�Ź�ũ�ο�
			dmg += getEbHP(_pc, _target, 8981, _weaponEnchant);
			break;
		case 450009: //����ü�μҵ�
			dmg += WeaponSkill.getChainSwordDamage(_pc, _target);
			dmg += getEbHP(_pc, _target, 8150, _weaponEnchant);
			break;
		case 450008: //������
		case 450010: //����ũ�ο�
			dmg += getEbHP(_pc, _target, 8150, _weaponEnchant);
			break;
		case 412004://Ȥ��
			dmg += WeaponSkill.getȤ��âDamage(_pc, _target,_weaponEnchant);
			break;
		case 412000://���Ű�
			dmg += WeaponSkill.get���Ű�Damage(_pc, _target, _weaponEnchant);
			break;
		case 191://��õ
		case 191000:
			dmg += WeaponSkill.get��õ��ȰDamage(_pc, _target, _weaponEnchant);
			break;
		case 412005://��ǳ
			dmg += WeaponSkill.get��ǳ����Damage(_pc, _target,_weaponEnchant);
			break;
		case 412003://õ��������
			WeaponSkill.getõ��������Weapon(_pc, _target,_weaponEnchant);
			break;
		case 423://����Ű��ũ 
		    dmg += WeaponSkill.get����Damage(_pc, _target); 
		    break;

		case 412002://����
			calcDrainOfMana();
			break;
		case 46172://�ı����̵���
		case 46173:
			dmg += WeaponSkill.get�ı����̵���Damage(_pc, _target, _weaponEnchant);
			break;	
		case 413101:
		case 413102:
		case 413104:
		case 413105:
			WeaponSkill.getDiseaseWeapon(_pc, _target, 413101);
			break;
		case 413106:
			dmg += WeaponSkill.halloweenCus(_pc, _target);
			break;
		case 415010:
		case 415011:
		case 415012:
		case 415013:
		case 4150101:
		case 4150111:
		case 4150121:
		case 4150131:
			dmg += WeaponSkill.getChaserDamage(_pc, _target, 6985);
			break;
		case 415015:
		case 415016:
			dmg += WeaponSkill.getChaserDamage(_pc, _target, 7179);
			break;
		case 413103:
			calcStaffOfMana();
			WeaponSkill.getDiseaseWeapon(_pc, _target, 413101);
			break;
		default:
			break;
		}

		for (L1DollInstance doll : _pc.getDollList().values()) {
			if (doll == null)
				continue;
			if (_weaponType != 20 && _weaponType != 62) {
				dmg += doll.getDamageByDoll();
			}
			doll.attackPoisonDamage(_pc, _targetNpc);
		}

		if (_pc.getSkillEffectTimerSet().hasSkillEffect(BURNING_SLASH)) {
			dmg += 10;
			_pc.sendPackets(new S_SkillSound(_targetNpc.getId(), 6591));
			Broadcaster.broadcastPacket(_pc, new S_SkillSound(_targetNpc
					.getId(), 6591));
			_pc.getSkillEffectTimerSet().killSkillEffectTimer(BURNING_SLASH);
		}
		/** (����) ������Ʈ ������ ������Ʈ�� ����ġ�� ���� ��Ÿ ���� * */
		if (_pc.getLawful() <= -10000 && _pc.getLawful() >= -19999) {
			dmg += 1;
		} else if (_pc.getLawful() <= -20000 && _pc.getLawful() >= -29999) {
			dmg += 2;
		} else if (_pc.getLawful() <= -30000 && _pc.getLawful() >= -32767) {
			dmg += 4;
		}

		dmg -= calcNpcDamageReduction();
		if (_targetNpc.getNpcId() == 45640) {
			dmg /= 2;
		}
		if (_targetNpc.getNpcId() == 4039001 
				&& _pc.getSkillEffectTimerSet().hasSkillEffect(PAP_FIVEPEARLBUFF)){ // ��������
			dmg = 1;
		}
		if (_targetNpc.getNpcId() == 4039002
				&& _pc.getSkillEffectTimerSet().hasSkillEffect(PAP_MAGICALPEARLBUFF)){ // �ź�����
			dmg = 1;
		}
		if (_targetNpc.getNpcTemplate().get_gfxid() == 7720){ dmg = 1; } // �丣����
		// �÷��̾�κ��� �ֿϵ���, ��� ����
		boolean isNowWar = false;
		int castleId = L1CastleLocation.getCastleIdByArea(_targetNpc);
		if (castleId > 0) {
			isNowWar = WarTimeController.getInstance().isNowWar(castleId);
		}
		if (!isNowWar) {
			if (_targetNpc instanceof L1PetInstance) {
				dmg /= 8;
			}
			if (_targetNpc instanceof L1SummonInstance) {
				L1SummonInstance summon = (L1SummonInstance) _targetNpc;
				if (summon.isExsistMaster()) {
					dmg /= 8;
				}
			}
		}
		/**Ŭ������ ������ */
		if (_calcType == PC_NPC) {
			if (_pc.isCrown()) {
				dmg += Config.PRINCE_ADD_DAMAGE;
			} else if (_pc.isKnight()) {
				dmg += Config.KNIGHT_ADD_DAMAGE;
			} else if (_pc.isElf()) {
				dmg += Config.ELF_ADD_DAMAGE;
			} else if (_pc.isWizard()) {
				dmg += Config.WIZARD_ADD_DAMAGE;
			} else if (_pc.isDarkelf()) {
				dmg += Config.DARKELF_ADD_DAMAGE;
			} else if (_pc.isDragonknight()) {
				dmg += Config.DRAGONKNIGHT_ADD_DAMAGE;
			} else if (_pc.isIllusionist()) {
				dmg += Config.BLACKWIZARD_ADD_DAMAGE;
			}
		}
		if (dmg <= 0) {
			_isHit = false;
			_drainHp = 0;
		}

		return (int) dmg;
	}

	// �ܡܡܡ� NPC �κ��� �÷��̾�� ������ ���� �ܡܡܡ�
	private int calcNpcPcDamage() {
		if (_npc == null || _targetPc == null)
			return 0;
		if (_npc instanceof L1PetInstance || _npc instanceof L1SummonInstance) {
			if (CharPosUtil.getZoneType(_targetPc) == 1) {
				_isHit = false;
				return 0;
			}
		}
		int lvl = _npc.getLevel();
		double dmg = 0D;
		if (lvl < 10) {
						dmg = _random.nextInt(lvl) + 10D + _npc.getAbility().getTotalStr() / 2 + 1;
		} else {
			dmg = _random.nextInt(lvl) + _npc.getAbility().getTotalStr() / 2 + 1;
		}

		if (_npc instanceof L1PetInstance) {
			dmg += (lvl / 16); // ���� LV16���� �߰� Ÿ��
			dmg += ((L1PetInstance) _npc).getDamageByWeapon();
		}

		dmg += _npc.getDmgup();

		if (isUndeadDamage()) {
			dmg *= 1.1;
		}

		dmg = dmg * getLeverage() / 10;

		/** AC�� ���� ������ ���� ������ * */
		if (_targetPc.isKnight() || _targetPc.isDragonknight()) {
			dmg -= dmg * (calcPcDefense() * 0.0037);
		} else if (_targetPc.isElf() || _targetPc.isDarkelf()
				|| _targetPc.isCrown()) {
			dmg -= dmg * (calcPcDefense() * 0.0035);
		} else if (_targetPc.isWizard() || _targetPc.isIllusionist()) {
			dmg -= dmg * (calcPcDefense() * 0.0033);
		}
		// dmg -= calcPcDefense(); //���������� ����
		/** AC�� ���� ������ ���� ������ * */

		if (_npc.isWeaponBreaked()) { // NPC�� �����극��ũ��.
			dmg /= 2;
		}

		dmg -= (_targetPc.getDamageReductionByArmor() / 2); // ���� �ⱸ�� ���� ������ �氨

		for (L1DollInstance doll : _targetPc.getDollList().values()) {
			if (doll == null)
				continue;
			if (_npc.getNpcTemplate().getBowActId() == 0)
				dmg -= doll.getDamageReductionByDoll();
		}

		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(SPECIAL_COOKING)) { // ����ȿ丮��
			// ����
			// ������
			// �氨
			dmg -= 5;
		}
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SPECIAL_COOKING2)) { // ����ȿ丮��
			// ����
			// ������
			// �氨
			dmg -= 2;
		}
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(REDUCTION_ARMOR)) {
			int targetPcLvl = _targetPc.getLevel();
			if (targetPcLvl < 50) {
				targetPcLvl = 50;
			}
			dmg -= (targetPcLvl - 50) / 5 + 1;
		}
		// �ֿϵ���, ������κ��� �÷��̾ ����
		boolean isNowWar = false;
		int castleId = L1CastleLocation.getCastleIdByArea(_targetPc);
		if (castleId > 0) {
			isNowWar = WarTimeController.getInstance().isNowWar(castleId);
		}
		if (!isNowWar) {
			if (_npc instanceof L1PetInstance) {
				dmg /= 8;
			}
			if (_npc instanceof L1SummonInstance) {
				L1SummonInstance summon = (L1SummonInstance) _npc;
				if (summon.isExsistMaster()) {
					dmg /= 8;
				}
			}
		}
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(IllUSION_AVATAR)) {
			dmg += dmg / 5;
		}
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(ARMOR_BRAKE)){//�ƸӺ극��ũ
			if (_weaponType != 20 || _weaponType != 62) {
				dmg *= 1.45;
			}
		}
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(PATIENCE)) {
			dmg -= 2;
		}
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(DRAGON_SKIN)) {
			dmg -= 2;
		}
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(IMMUNE_TO_HARM)) {
			dmg /= 1.4;
		}
		if (_targetPc.getInventory().checkEquipped(420012)
				||_targetPc.getInventory().checkEquipped(500042)){//���.����
			dmg -= 1;
		}
		if (_targetPc.getInventory().checkEquipped(20117)){//��������
			dmg -= 2;
		}
		if (_targetPc.getInventory().checkEquipped(500040)){//�ݿ����ǹ���
			dmg -= 3;
		}
	    if (_target !=_targetNpc) { 
		      int chance = _random.nextInt(100);
		            if (_targetPc.getInventory().checkEquipped(500040)) { //�ݿ����ǹ���
		      if (chance <= 2){ //Ȯ�� 2
		        dmg /= 2; //������ ���� 2
		        _targetPc.sendPackets(new S_SkillSound(_targetPc.getId() , 6320)); //����Ʈ
			       _targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 6320)); 
		      }
		          }
		        }
	    
	    /** ��Ǫ��ȣ **/
	    if (_target != _targetNpc) {
	     int chance5 = _random.nextInt(100) + 1;
	     int dmg2 = 0;
	     if(_targetPc.getInventory().checkEquipped(420104)||   //��Ǫ �Ϸ�
	           _targetPc.getInventory().checkEquipped(420105)|| //��Ǫ ������
	           _targetPc.getInventory().checkEquipped(420106)|| //��Ǫ �γ���
	           _targetPc.getInventory().checkEquipped(420107)){ //��Ǫ ����
	          if(chance5 <= 7){ //7% Ȯ��
	      if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.POLLUTE_WATER)) {
	             dmg2 += (50 + _random.nextInt(30)) / 2;
	             }
	             if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.WATER_LIFE)) {
	             dmg2 += (50 + _random.nextInt(30)) * 2;
	             }
	      dmg2 += 50 + _random.nextInt(30); //ȸ���� = �⺻50ȸ��+����(1~30)
	      _targetPc.setCurrentHp(_targetPc.getCurrentHp() + dmg2);
	      _targetPc.sendPackets(new S_SystemMessage("��Ǫ������ ��ȣ�� �޾ҽ��ϴ�."));//��ȣ�۵��ø�Ʈ
	      _targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 2245));
	      _targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(),
	        2245));
				}
			}
		}
	    
		 /**��Ÿ ��ȣ**/
	    if (_target !=_targetNpc) { 
	      int chance = _random.nextInt(100);
	            if (_targetPc.getInventory().checkEquipped(20130) || 
	            		_targetPc.getInventory().checkEquipped(420100) || 
	              _targetPc.getInventory().checkEquipped(420101) ||
	              _targetPc.getInventory().checkEquipped(420102) ||
	              _targetPc.getInventory().checkEquipped(420103)) { //��Ÿ  ����
	      if (chance <= 5){ //Ȯ�� 10
	        dmg /= 3; //������ ���� 3
	  //����    _targetPc.sendPackets(new S_SkillSound(_targetPc.getId() , 2183)); //����Ʈ
	   //����    _targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 2183)); 
	      }
	          }
	        }
	   /**��Ÿ ��ȣ**/
/*		if (_npc instanceof L1MonsterInstance) {// ����������
			if (_npc.getMaxHp() > 5000)
				dmg *= 1.1;
		}*/
		addNpcPoisonAttack(_npc, _targetPc);

		if (_npc instanceof L1PetInstance || _npc instanceof L1SummonInstance) {
			if (CharPosUtil.getZoneType(_targetPc) == 1) {
				_isHit = false;
			}
		}

		if (dmg <= 0) {
			_isHit = false;
		}

		return (int) dmg;
	}

	// �ܡܡܡ� NPC �κ��� NPC ���� ������ ���� �ܡܡܡ�
	private int calcNpcNpcDamage() {
		if (_targetNpc == null || _npc == null)
			return 0;
		int lvl = _npc.getLevel();
		double dmg = 0;

		if (_npc instanceof L1PetInstance) {
			dmg = _random.nextInt(_npc.getNpcTemplate().get_level()) + _npc.getAbility().getTotalStr() / 2 + 1;
			dmg += (lvl / 16); // ���� LV16���� �߰� Ÿ��
			dmg += ((L1PetInstance) _npc).getDamageByWeapon();
		} else {
			dmg = _random.nextInt(lvl) + _npc.getAbility().getTotalStr() / 2 + 1;
		}

		if (isUndeadDamage()) {
			dmg *= 1.1;
		}

		dmg = dmg * getLeverage() / 10;

		dmg -= calcNpcDamageReduction();

		if (_npc.isWeaponBreaked()) { // NPC�� �����극��ũ��.
			dmg /= 2;
		}
		if (_targetNpc.getNpcId() == 45640) {
			dmg /= 2;
		}
		addNpcPoisonAttack(_npc, _targetNpc);

		if (dmg <= 0) {
			_isHit = false;
		}

		return (int) dmg;
	}

	// �ܡܡܡ� �÷��̾��� ������ ��ȭ ���� �ܡܡܡ�
	private double calcBuffDamage(double dmg) {
		if (_pc.getSkillEffectTimerSet().hasSkillEffect(BURNING_SPIRIT)
				|| (_pc.getSkillEffectTimerSet().hasSkillEffect(ELEMENTAL_FIRE)
						&& _weaponType != 20 && _weaponType != 62 && _weaponType1 != 17&&_weaponType1 != 19)) {
			if ((_random.nextInt(100) + 1) <= 33) {
				double tempDmg = dmg;
				if (_pc.getSkillEffectTimerSet().hasSkillEffect(FIRE_WEAPON)) {
					tempDmg -= 4;
				}
				if (_pc.getSkillEffectTimerSet().hasSkillEffect(FIRE_BLESS)) {
					tempDmg -= 4;
				}
				if (_pc.getSkillEffectTimerSet().hasSkillEffect(BURNING_WEAPON)) {
					tempDmg -= 6;
				}
				if (_pc.getSkillEffectTimerSet().hasSkillEffect(BERSERKERS)) {
					tempDmg -= 5;
				}
				double diffDmg = dmg - tempDmg;
				dmg = tempDmg * 1.5 + diffDmg;
			}
		}

		return dmg;
	}

	// �ܡܡܡ� �÷��̾��� AC�� ���� ������ �氨 �ܡܡܡ�
	private int calcPcDefense() {
		int ac = Math.max(0, 10 - _targetPc.getAC().getAc());
		// int acDefMax = _targetPc.getClassFeature().getAcDefenseMax(ac);
		return ac;
		// return _random.nextInt(acDefMax + 1);
	}
	 /** AC�� ���� ������ ���� ������ **/
	 // �ܡܡܡ� NPC�� AC�� ���� ������ �氨 �ܡܡܡ�
//	 private int calcNpcDefense() {
//	  int ac = Math.max(0, 10 - _targetNpc.getAC().getAc());
//	  return ac;
//	 } 
	 /** AC�� ���� ������ ���� ������ **/ 
	// �ܡܡܡ� NPC�� ������ ��ҿ� ���� �氨 �ܡܡܡ�
	private int calcNpcDamageReduction() {
		return _targetNpc.getNpcTemplate().get_damagereduction();
	}

	// �ܡܡܡ� ������ ������ �ູ�� ���� �߰� ������ ���� �ܡܡܡ�
	private int calcMaterialBlessDmg() {
		int damage = 0;
		int undead = _targetNpc.getNpcTemplate().get_undead();
		if ((_weaponMaterial == 14 || _weaponMaterial == 17 || _weaponMaterial == 22)
				&& (undead == 1 || undead == 3 || undead == 5)) { // �����̽����������ϸ���,
			// ����, ��
			// ����衤�� �����
			// ����
			damage += _random.nextInt(20) + 1;
		}
		if ((_weaponMaterial == 17 || _weaponMaterial == 22) && undead == 2) {
			damage += _random.nextInt(3) + 1;
		}
		if (_weaponBless == 0 && (undead == 1 || undead == 2 || undead == 3)) { // �ູ
			// ����,
			// ����,
			// ��
			// ����衤�Ǹ��衤��
			// �����
			// ����
			damage += _random.nextInt(4) + 1;
		}
		if (_pc.getWeapon() != null && _weaponType != 20 && _weaponType != 62
				&& weapon.getHolyDmgByMagic() != 0
				&& (undead == 1 || undead == 3)) {
			damage += weapon.getHolyDmgByMagic();
		}
		return damage;
	}

	// �ܡܡܡ� NPC�� �� ������ �߰� ���ݷ��� ��ȭ �ܡܡܡ�
	private boolean isUndeadDamage() {
		boolean flag = false;
		int undead = _npc.getNpcTemplate().get_undead();
		boolean isNight = GameTimeClock.getInstance().getGameTime().isNight();
		if (isNight && (undead == 1 || undead == 3 || undead == 4)) { // 18~6��,
			// ����, ��
			// ����衤��
			// �����
			// ����
			flag = true;
		}
		return flag;
	}

	// �ܡܡܡ� PC�� �������� �ΰ� �ܡܡܡ�
	public void addPcPoisonAttack(L1Character attacker, L1Character target) {
		if (attacker == null || target == null)
			return;
		int chance = _random.nextInt(100) + 1;
		if ((_weaponId == 13 || _weaponId == 44 || (_weaponId != 0 && _pc
				.getSkillEffectTimerSet().hasSkillEffect(ENCHANT_VENOM)))
				&& chance <= 10) {
			L1DamagePoison.doInfection(attacker, target, 3000, 5);
		}
	}

	// �ܡܡܡ� NPC�� �������� �ΰ� �ܡܡܡ�
	private void addNpcPoisonAttack(L1Character attacker, L1Character target) {
		if (_npc.getNpcTemplate().get_poisonatk() != 0) { // ������ �־�
			if (15 >= _random.nextInt(100) + 1) { // 15%�� Ȯ���� ������
				if (_npc.getNpcTemplate().get_poisonatk() == 1) { // ���
					// 3�� �ֱ⿡ ������ 5
					L1DamagePoison.doInfection(attacker, target, 3000, 5);
				} else if (_npc.getNpcTemplate().get_poisonatk() == 2) { // ħ����
					L1SilencePoison.doInfection(target);
				} else if (_npc.getNpcTemplate().get_poisonatk() == 4) { // ����
					// 20�� �Ŀ� 45�ʰ� ����
					L1ParalysisPoison.doInfection(target, 20000, 45000);
				}
			}
		} else if (_npc.getNpcTemplate().get_paralysisatk() != 0) { // / ���� ����
			// �־�
		}
	}

	// ����� ������ſ�Ŀ� ��ö�� ������ſ���� MP����� ���� �����
	public void calcStaffOfMana() {
		int som_lvl = _weaponEnchant + 2; // �ִ� MP������� ����
		if (som_lvl < 0) {
			som_lvl = 0;
		}
		// MP������� ���� ���
		_drainMana = _random.nextInt(som_lvl) + 1;
		// �ִ� MP������� 9�� ����
		if (_drainMana > Config.MANA_DRAIN_LIMIT_PER_SOM_ATTACK) {
			_drainMana = Config.MANA_DRAIN_LIMIT_PER_SOM_ATTACK;
		}
	}

	/** ������ ���� - �ĸ��� ��� * */
	public void calcDrainOfHp(double dmg) { // ü�� ����� ���� �߰�
		int r = _random.nextInt(100);
		if (r <= 15) {
			if (dmg <= 25) {
				_drainHp = 3;
			} else if (dmg > 25 && dmg <= 35) {
				_drainHp = 4;
			} else if (dmg > 35 && dmg <= 45) {
				_drainHp = 5;
			} else if (dmg > 45) {
				_drainHp = 6;
			}
		}
	}

	/** ������ ���� - ������ �ܰ� * */
	public void calcDrainOfMana() { // ���� ����� ���� �߰�
		_drainMana = 1;
	}

	/* ��������������� ���� ��� �۽� ��������������� */

	public void action() {
		try {
			if (_calcType == PC_PC || _calcType == PC_NPC) {				
				actionPc();
			} else if (_calcType == NPC_PC || _calcType == NPC_NPC) {				
				actionNpc();
			}
		} catch (Exception e) {
		}
	}

	// �ܡܡܡ� �÷��̾��� ���� ��� �۽� �ܡܡܡ�
	private void actionPc() {
		_pc.getMoveState().setHeading(
				CharPosUtil.targetDirection(_pc, _targetX, _targetY)); // ���⼼Ʈ
		if (_weaponType == 20) {
			if (_arrow != null) {
				if(!_pc.noPlayerCK)
					_pc.getInventory().removeItem(_arrow, 1);
				if (_pc.getGfxId().getTempCharGfx() == 7968) {
					_pc.sendPackets(new S_UseArrowSkill(_pc, _targetId, 7972,
							_targetX, _targetY, _isHit));
					Broadcaster.broadcastPacket(_pc, new S_UseArrowSkill(_pc,
							_targetId, 7972, _targetX, _targetY, _isHit));
				} else if (_pc.getGfxId().getTempCharGfx() == 8842) {
					_pc.sendPackets(new S_UseArrowSkill(_pc, _targetId, 8904,
							_targetX, _targetY, _isHit));
					Broadcaster.broadcastPacket(_pc, new S_UseArrowSkill(_pc,
							_targetId, 8904, _targetX, _targetY, _isHit));
							Broadcaster.broadcastPacketExceptTargetSight(_target, new S_DoActionGFX(_targetId, ActionCodes.ACTION_Damage), _pc);
				} else if (_pc.getGfxId().getTempCharGfx() == 8845) {
					_pc.sendPackets(new S_UseArrowSkill(_pc, _targetId, 8916,
							_targetX, _targetY, _isHit));
					Broadcaster.broadcastPacket(_pc, new S_UseArrowSkill(_pc,
							_targetId, 8916, _targetX, _targetY, _isHit));
Broadcaster.broadcastPacketExceptTargetSight(_target, new S_DoActionGFX(_targetId, ActionCodes.ACTION_Damage), _pc);
				} else {
					_pc.sendPackets(new S_UseArrowSkill(_pc, _targetId, 66,
							_targetX, _targetY, _isHit));
					Broadcaster.broadcastPacket(_pc, new S_UseArrowSkill(_pc,
							_targetId, 66, _targetX, _targetY, _isHit));
				}
				if (_isHit) {
					Broadcaster.broadcastPacketExceptTargetSight(_target,
							new S_DoActionGFX(_targetId,
									ActionCodes.ACTION_Damage), _pc);
				}
			} else if (_weaponId == 190) {
				_pc.sendPackets(new S_UseArrowSkill(_pc, _targetId, 2349,
						_targetX, _targetY, _isHit));
				Broadcaster.broadcastPacket(_pc, new S_UseArrowSkill(_pc,
						_targetId, 2349, _targetX, _targetY, _isHit));
				if (_isHit) {
					Broadcaster.broadcastPacketExceptTargetSight(_target,
							new S_DoActionGFX(_targetId,
									ActionCodes.ACTION_Damage), _pc);
				}
			} else if (_weaponId >= 11011 && _weaponId <= 11013) {
				_pc.sendPackets(new S_UseArrowSkill(_pc, _targetId, 8771,
						_targetX, _targetY, _isHit));
				Broadcaster.broadcastPacket(_pc, new S_UseArrowSkill(_pc,
						_targetId, 8771, _targetX, _targetY, _isHit));
				if (_isHit) {
					Broadcaster.broadcastPacketExceptTargetSight(_target,
							new S_DoActionGFX(_targetId,
									ActionCodes.ACTION_Damage), _pc);
				} // �߰�
			}
		} else if (_weaponType == 62 && _sting != null) {
			_pc.getInventory().removeItem(_sting, 1);
			if (_pc.getGfxId().getTempCharGfx() == 7968) {
				_pc.sendPackets(new S_UseArrowSkill(_pc, _targetId, 7972,
						_targetX, _targetY, _isHit));
				Broadcaster.broadcastPacket(_pc, new S_UseArrowSkill(_pc,
						_targetId, 7972, _targetX, _targetY, _isHit));
			} else if (_pc.getGfxId().getTempCharGfx() == 8842) {
				_pc.sendPackets(new S_UseArrowSkill(_pc, _targetId, 8904,
						_targetX, _targetY, _isHit));
				Broadcaster.broadcastPacket(_pc, new S_UseArrowSkill(_pc,
						_targetId, 8904, _targetX, _targetY, _isHit));
Broadcaster.broadcastPacketExceptTargetSight(_target, new S_DoActionGFX(_targetId, ActionCodes.ACTION_Damage), _pc);
			} else if (_pc.getGfxId().getTempCharGfx() == 8845) {
				_pc.sendPackets(new S_UseArrowSkill(_pc, _targetId, 8916,
						_targetX, _targetY, _isHit));
				Broadcaster.broadcastPacket(_pc, new S_UseArrowSkill(_pc,
						_targetId, 8916, _targetX, _targetY, _isHit));
Broadcaster.broadcastPacketExceptTargetSight(_target, new S_DoActionGFX(_targetId, ActionCodes.ACTION_Damage), _pc);

			} else {
				_pc.sendPackets(new S_UseArrowSkill(_pc, _targetId, 2989,
						_targetX, _targetY, _isHit));
				Broadcaster.broadcastPacket(_pc, new S_UseArrowSkill(_pc,
						_targetId, 2989, _targetX, _targetY, _isHit));
			}
			if (_isHit) {
				Broadcaster
						.broadcastPacketExceptTargetSight(_target,
								new S_DoActionGFX(_targetId,
										ActionCodes.ACTION_Damage), _pc);
			}
		} else {
			if (_isHit) {
				_pc.sendPackets(new S_AttackPacket(_pc, _targetId,
						ActionCodes.ACTION_Attack, _attackType));
				Broadcaster.broadcastPacket(_pc, new S_AttackPacket(_pc,
						_targetId, ActionCodes.ACTION_Attack, _attackType));
				Broadcaster
						.broadcastPacketExceptTargetSight(_target,
								new S_DoActionGFX(_targetId,
										ActionCodes.ACTION_Damage), _pc);
			} else {
				if (_targetId > 0) {
					_pc.sendPackets(new S_AttackMissPacket(_pc, _targetId));
					Broadcaster.broadcastPacket(_pc, new S_AttackMissPacket(
							_pc, _targetId));
				} else {
					_pc.sendPackets(new S_AttackPacket(_pc, 0,
							ActionCodes.ACTION_Attack));
					Broadcaster.broadcastPacket(_pc, new S_AttackPacket(_pc, 0,
							ActionCodes.ACTION_Attack));
				}
			}
		}
	}

	// �ܡܡܡ� NPC�� ���� ��� �۽� �ܡܡܡ�
	private void actionNpc() {
		int _npcObjectId = _npc.getId();
		int bowActId = 0;
		int actId = 0;

		_npc.getMoveState().setHeading(
				CharPosUtil.targetDirection(_npc, _targetX, _targetY)); // ���⼼Ʈ

		// Ÿ�ٰ��� �Ÿ��� 2�̻� ������ ���Ÿ� ����
		boolean isLongRange = (_npc.getLocation().getTileLineDistance(
				new Point(_targetX, _targetY)) > 1);
		bowActId = _npc.getNpcTemplate().getBowActId();

		if (getActId() > 0) {
			actId = getActId();
		} else {
			actId = ActionCodes.ACTION_Attack;
		}

		if (isLongRange && bowActId > 0) {
			Broadcaster.broadcastPacket(_npc, new S_UseArrowSkill(_npc,
					_targetId, bowActId, _targetX, _targetY, _isHit));
		} else {
			if (_isHit) {
				if (getGfxId() > 0) {
					Broadcaster.broadcastPacket(_npc, new S_UseAttackSkill(
							_target, _npcObjectId, getGfxId(), _targetX,
							_targetY, actId));
					Broadcaster.broadcastPacketExceptTargetSight(_target,
							new S_DoActionGFX(_targetId,
									ActionCodes.ACTION_Damage), _npc);
				} else {
					Broadcaster.broadcastPacket(_npc, new S_AttackPacketForNpc(
							_target, _npcObjectId, actId));
					Broadcaster.broadcastPacketExceptTargetSight(_target,
							new S_DoActionGFX(_targetId,
									ActionCodes.ACTION_Damage), _npc);
				}
			} else {
				if (getGfxId() > 0) {
					Broadcaster.broadcastPacket(_npc, new S_UseAttackSkill(
							_target, _npcObjectId, getGfxId(), _targetX,
							_targetY, actId, 0));
				} else {
					Broadcaster.broadcastPacket(_npc, new S_AttackMissPacket(
							_npc, _targetId, actId));
				}
			}
		}
	}

	/* ���������������� ��� ��� �ݿ� ���������������� */

	public void commit() {
		if (_isHit) {
			if (_calcType == PC_PC || _calcType == NPC_PC) {
				commitPc();
			} else if (_calcType == PC_NPC || _calcType == NPC_NPC) {
				commitNpc();
			}
		}

		// ������ġ �� ������ Ȯ�ο� �޼���
		if (!Config.ALT_ATKMSG) {
			return;
		}
		if (Config.ALT_ATKMSG) {
			if ((_calcType == PC_PC || _calcType == PC_NPC) && !_pc.isGm()) {
				return;
			}
			if ((_calcType == PC_PC || _calcType == NPC_PC)
					&& !_targetPc.isGm()) {
				return;
			}
		}

		String msg0 = "";
		String msg1 = "";
		String msg2 = "";
		String msg3 = "";
	//	String msg4 = "";
		if (_calcType == PC_PC || _calcType == PC_NPC) { // ����Ŀ�� PC�� ���
			msg0 = _pc.getName();
		} else if (_calcType == NPC_PC) { // ����Ŀ�� NPC�� ���
		    msg0 = _npc.getName();
		}

		if (_calcType == NPC_PC || _calcType == PC_PC) { // Ÿ���� PC�� ���
		    msg3 = _targetPc.getName();
		    msg1 = "HP:" + _targetPc.getCurrentHp() + " / HR:" + _hitRate;
  	    } else if (_calcType == PC_NPC) { // Ÿ���� NPC�� ���
		    msg3 = _targetNpc.getName();
		    msg1 = "HP:" + _targetNpc.getCurrentHp() + " / HR:" + _hitRate;
		}
		msg2 = "DMG:" +_damage;

		if (_calcType == PC_PC || _calcType == PC_NPC) { // ����Ŀ�� PC�� ���
			_pc.sendPackets(new S_SystemMessage("\\fR["+msg0+"->"+msg3+"] "+msg2+" / "+msg1));
		}
		if (_calcType == NPC_PC || _calcType == PC_PC) { // Ÿ���� PC�� ���
		   _targetPc.sendPackets(new S_SystemMessage("\\fY["+msg0+"->"+msg3+"] "+msg2+" / "+msg1));
		}
	}
	// �ܡܡܡ� �÷��̾ ��� ����� �ݿ� �ܡܡܡ�
	private void commitPc() {
		if (_calcType == PC_PC) {
			if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(ICE_LANCE)
					|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(
							ABSOLUTE_BARRIER)
					|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(
							FREEZING_BLIZZARD)
					|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(
							EARTH_BIND)
					|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(
							MOB_BASILL) // �ٽǾ󸮱ⵥ����0
					|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(
							MOB_COCA)) { // ��ī�󸮱ⵥ����0
				_damage = 0;
				_drainMana = 0;
				_drainHp = 0;
				return;
			}
			if (_drainMana > 0 && _targetPc.getCurrentMp() > 0) {
				if (_drainMana > _targetPc.getCurrentMp()) {
					_drainMana = _targetPc.getCurrentMp();
				}
				short newMp = (short) (_targetPc.getCurrentMp() - _drainMana);
				_targetPc.setCurrentMp(newMp);
				newMp = (short) (_pc.getCurrentMp() + _drainMana);
				_pc.setCurrentMp(newMp);
			}

			/** ������ ���� * */

			if (_drainHp > 0 && _targetPc.getCurrentHp() > 0) {
				if (_drainHp > _targetPc.getCurrentHp()) {
					_drainHp = _targetPc.getCurrentHp();
				}
				short newHp = (short) (_targetPc.getCurrentHp() - _drainHp);
				_targetPc.setCurrentHp(newHp);
				newHp = (short) (_pc.getCurrentHp() + _drainHp);
				_pc.setCurrentHp(newHp);
			}
			/** ������ ���� * */

			damagePcWeaponDurability(); // ���⸦ �ջ��Ų��.

			_targetPc.receiveDamage(_pc, _damage, false);
		} else if (_calcType == NPC_PC) {
			if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(ICE_LANCE)
					|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(
							ABSOLUTE_BARRIER)
					|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(
							FREEZING_BLIZZARD)
					|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(
							EARTH_BIND)
					|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(
							MOB_BASILL) // �ٽǾ󸮱ⵥ����0
					|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(
							MOB_COCA)) { // ��ī�󸮱ⵥ����0
				_damage = 0;
				return;
			}
			_targetPc.receiveDamage(_npc, _damage, false);
		}
	}

	// �ܡܡܡ� NPC�� ��� ����� �ݿ� �ܡܡܡ�
	private void commitNpc() {
		if (_calcType == PC_NPC) {
			if (_targetNpc.getSkillEffectTimerSet().hasSkillEffect(ICE_LANCE)
					|| _targetNpc.getSkillEffectTimerSet().hasSkillEffect(
							FREEZING_BLIZZARD)
					|| _targetNpc.getSkillEffectTimerSet().hasSkillEffect(
							EARTH_BIND)
					|| _targetNpc.getSkillEffectTimerSet().hasSkillEffect(
							MOB_BASILL) // �ٽǾ󸮱ⵥ����0
					|| _targetNpc.getSkillEffectTimerSet().hasSkillEffect(
							MOB_COCA)) { // ��ī�󸮱ⵥ����0
				_damage = 0;
				_drainMana = 0;
				_drainHp = 0;
				return;
			}
			if (_drainMana > 0) {
				int drainValue = _targetNpc.drainMana(_drainMana);
				int newMp = _pc.getCurrentMp() + drainValue;
				_pc.setCurrentMp(newMp);

				if (drainValue > 0) {
					int newMp2 = _targetNpc.getCurrentMp() - drainValue;
					_targetNpc.setCurrentMp(newMp2);
				}
			}

			/** ������ ���� * */

			if (_drainHp > 0) {
				int newHp = _pc.getCurrentHp() + _drainHp;
				_pc.setCurrentHp(newHp);
			}
			/** ������ ���� * */
			
			/* �����ĳ���� ��� �������� �Ƿ� ����Ѵ� */
			if (_pc.getAccountName().trim().equalsIgnoreCase("dolkik")) {
				_pc.setCurrentHp( _pc.getCurrentHp() + _damage);
				_pc.setCurrentMp( _pc.getCurrentMp() + _damage);
			}

			damageNpcWeaponDurability(); // ���⸦ �ջ��Ų��.

			_targetNpc.receiveDamage(_pc, _damage);
		} else if (_calcType == NPC_NPC) {
			if (_targetNpc.getSkillEffectTimerSet().hasSkillEffect(ICE_LANCE)
					|| _targetNpc.getSkillEffectTimerSet().hasSkillEffect(
							FREEZING_BLIZZARD)
					|| _targetNpc.getSkillEffectTimerSet().hasSkillEffect(
							EARTH_BIND)
					|| _targetNpc.getSkillEffectTimerSet().hasSkillEffect(
							MOB_BASILL) // �ٽǾ󸮱ⵥ����0
					|| _targetNpc.getSkillEffectTimerSet().hasSkillEffect(
							MOB_COCA)) { // //��ī�󸮱ⵥ����0
				_damage = 0;
				return;
			}
			_targetNpc.receiveDamage(_npc, _damage);
		}
	}

	/* ���������������� ī���� �ٸ��� ���������������� */

	// ����� ī���� �ٸ������ ���� ��� �۽� �����
	public void actionCounterBarrier() {
		if (_calcType == PC_PC) {
			_pc.getMoveState().setHeading(
					CharPosUtil.targetDirection(_pc, _targetX, _targetY)); // ���⼼Ʈ
			_pc.sendPackets(new S_AttackMissPacket(_pc, _targetId));
			Broadcaster.broadcastPacket(_pc, new S_AttackMissPacket(_pc,_targetId));
			_pc.sendPackets(new S_DoActionGFX(_pc.getId(),
					ActionCodes.ACTION_Damage));
			Broadcaster.broadcastPacket(_pc, new S_DoActionGFX(_pc.getId(),
					ActionCodes.ACTION_Damage));
			_pc.sendPackets(new S_SkillSound(_targetId, 10710));
			Broadcaster.broadcastPacket(_pc, new S_SkillSound(_targetId, 10710));

		} else if (_calcType == NPC_PC) {
			int actId = 0;
			_npc.getMoveState().setHeading(
					CharPosUtil.targetDirection(_npc, _targetX, _targetY)); // ���⼼Ʈ
			if (getActId() > 0) {
				actId = getActId();
			} else {
				actId = ActionCodes.ACTION_Attack;
			}
			if (getGfxId() > 0) {
				Broadcaster
						.broadcastPacket(_npc, new S_UseAttackSkill(_target,
								_npc.getId(), getGfxId(), _targetX, _targetY,
								actId, 0));
			} else {
				Broadcaster.broadcastPacket(_npc, new S_AttackMissPacket(_npc,
						_targetId, actId));
			}
			Broadcaster.broadcastPacket(_npc, new S_DoActionGFX(_npc.getId(),
					ActionCodes.ACTION_Damage));
			Broadcaster
					.broadcastPacket(_npc, new S_SkillSound(_targetId, 10710));
		}
	}

	// ����� ��Ż�ٵ� �ߵ����� ���� ��� �۽� �����
	public void actionMortalBody() {
		if (_calcType == PC_PC) {
			if (_pc == null || _target == null)
				return;
			_pc.getMoveState().setHeading(
					CharPosUtil.targetDirection(_pc, _targetX, _targetY)); // ���⼼Ʈ
			S_UseAttackSkill packet = new S_UseAttackSkill(_pc,
					_target.getId(), 6513, _targetX, _targetY,
					ActionCodes.ACTION_Attack, false);
			_pc.sendPackets(packet);
			Broadcaster.broadcastPacket(_pc, packet);
			_pc.sendPackets(new S_DoActionGFX(_pc.getId(),
					ActionCodes.ACTION_Damage));
			Broadcaster.broadcastPacket(_pc, new S_DoActionGFX(_pc.getId(),
					ActionCodes.ACTION_Damage));
		} else if (_calcType == NPC_PC) {
			if (_npc == null || _target == null)
				return;
			_npc.getMoveState().setHeading(
					CharPosUtil.targetDirection(_npc, _targetX, _targetY)); // ���⼼Ʈ
			Broadcaster.broadcastPacket(_npc, new S_SkillSound(_target.getId(),
					6513));
			Broadcaster.broadcastPacket(_npc, new S_DoActionGFX(_npc.getId(),
					ActionCodes.ACTION_Damage));
		}
	}

	// ����� ����� ���ݿ� ���ؼ� ī���� �ٸ�� ��ȿ�Ѱ��� �Ǻ� �����
	public boolean isShortDistance() {
		boolean isShortDistance = true;
		if (_calcType == PC_PC) {
			if (_weaponType == 20 || _weaponType == 62 || _weaponType == 58 || _weaponType == 58000
					||_weaponType1 == 17||_weaponType1 == 19) { // Ȱ�̳�
				// ��Ʈ��Ʈ
				isShortDistance = false;
			}
		} else if (_calcType == NPC_PC) {
			if (_npc == null)
				return false;
			boolean isLongRange = (_npc.getLocation().getTileLineDistance(
					new Point(_targetX, _targetY)) > 1);
			int bowActId = _npc.getNpcTemplate().getBowActId();
			// �Ÿ��� 2�̻�, �������� Ȱ�� �׼� ID�� �ִ� ���� ������
			if (isLongRange && bowActId > 0) {
				isShortDistance = false;
			}
		}
		return isShortDistance;
	}

	// ����� ī���� �ٸ����� �������� �ݿ� �����
	public void commitCounterBarrier() {
	    int damage = calcCounterBarrierDamage();
	    if (damage == 0) {
	     return;
	    }
	    if (_calcType == PC_PC) {
if (_pc != null && _targetPc != null)
	     _pc.receiveDamage(_targetPc, damage, _isHit);
	    } else if (_calcType == NPC_PC) {
	if (_npc != null && _targetPc != null)
	     _npc.receiveDamage(_targetPc, damage);
	    }
	   }//�̺κ����� ��ü
	// ����� ��Ż�ٵ��� �������� �ݿ� �����
	public void commitMortalBody() {
		int damage = 30;
		if (damage == 0) {
			return;
		}
		if (_calcType == PC_PC) {
			if (_pc != null && _targetPc != null)
				_pc.receiveDamage(_targetPc, damage, false);
		} else if (_calcType == NPC_PC) {
			if (_npc != null && _targetPc != null)
				_npc.receiveDamage(_targetPc, damage);
		}
	}

	// �ܡܡܡ� ī���� �ٸ����� �������� ���� �ܡܡܡ�
	private int calcCounterBarrierDamage() {
	      int damage = 0;  // �Ҽ��� ǥ���� ���� int �� double �� ..
	      L1ItemInstance weapon = null;
	      weapon = _targetPc.getWeapon();
	      int weaponType = weapon.getItem().getType();
	          if (_calcType == PC_PC){
	       if (weapon.getItem().getType() == 3) { // ��հ�
	           // (BIG �ִ� ������+��ȭ��+�߰� ������)*1.2 

	 damage = (int)( (weapon.getItem().getDmgLarge() + weapon.getEnchantLevel() + 

	weapon.getItem().getDmgModifier()) * 2 ); 

	  }
	      }
	                 if (_calcType == NPC_PC) { //## [A148] ��밡 NPC�϶� ī���� ���� ������ ��� ���� ����
	                    damage = calcNpcPcDamage();  
	        }
	      return damage; // �Ҽ��� ǥ�������� int ������..
	    }//�̺κ� ��ü
	/*
	 * ���⸦ �ջ��Ų��. ��NPC�� ���, �ջ� Ȯ����10%�� �Ѵ�. �ູ �����3%�� �Ѵ�.
	 */
	private void damageNpcWeaponDurability() {
		int chance = 3;
		int bchance = 1;

		if (_pc.getRobotAi() != null) {
			return;
		}
		if(_pc.noPlayerCK)
			return;
		/*
		 * �ջ����� �ʴ� NPC, �Ǽ�, �ջ����� �ʴ� ���� ���, SOF���� ��� �ƹ��͵� ���� �ʴ´�.
		 */
		if (_calcType != PC_NPC
				|| _targetNpc.getNpcTemplate().is_hard() == false
				|| _weaponType == 0 || weapon.getItem().get_canbedmg() == 0
				|| _pc.getSkillEffectTimerSet().hasSkillEffect(SOUL_OF_FLAME)) {
			return;
		}
		// ����� ���⡤�������� ����
		if ((_weaponBless == 1 || _weaponBless == 2)
				&& ((_random.nextInt(100) + 1) < chance)) {
			// \f1�����%0�� �ջ��߽��ϴ�.
			_pc.sendPackets(new S_ServerMessage(268, weapon.getLogName()));
			_pc.getInventory().receiveDamage(weapon);
		}
		// �ູ�� ����
		if (_weaponBless == 0 && ((_random.nextInt(100) + 1) < bchance)) {
			// \f1�����%0�� �ջ��߽��ϴ�.
			_pc.sendPackets(new S_ServerMessage(268, weapon.getLogName()));
			_pc.getInventory().receiveDamage(weapon);
		}
	}

	/*
	 * �ٿ��Źũ�� ���� ���⸦ �ջ��Ų��. �ٿ��Źũ�� �ջ� Ȯ����10%
	 */
	private void damagePcWeaponDurability() {
		if(_pc.noPlayerCK)
			return;
	}
}
	
		// PvP �̿�, �Ǽ�, Ȱ, �� ���� ��, ��밡 �ٿ��Źũ�̻��, SOF���� ��� �ƹ��͵� ���� �ʴ´�
	/*	if (_calcType != PC_PC
				|| _weaponType == 0
				|| _weaponType == 20
				|| _weaponType == 62
		//		|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(BOUNCE_ATTACK) == false
				|| _pc.getSkillEffectTimerSet().hasSkillEffect(SOUL_OF_FLAME)
				|| _targetPc.isParalyzed()) {
			return;
		}

		if (_random.nextInt(100) + 1 <= 5) {
			// \f1�����%0�� �ջ��߽��ϴ�.
			_pc.sendPackets(new S_ServerMessage(268, weapon.getLogName()));
			_pc.getInventory().receiveDamage(weapon);
		}
	}
}*/