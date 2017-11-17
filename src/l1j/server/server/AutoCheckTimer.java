package l1j.server.server;

import java.util.Random;

import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_Disconnect;

// �����÷��̾� üũ �Լ�
public class AutoCheckTimer implements Runnable {

	private static Random r = new Random();

	public static int aCode;

	private static AutoCheckTimer _instance;

	public static AutoCheckTimer getInstance() {
		if (_instance == null) {
			_instance = new AutoCheckTimer();
		}
		return _instance;
	}

	public void run() {
		
		try {

			while (true) {

				aCode = r.nextInt(9000) + 1000;
				Thread.sleep(60000 * 2);
				SetnCheck();// SetnCheck 
				Thread.sleep(60000 * 5); 
				Redo(); 
				Thread.sleep(60000 * 5);
				Discon();
				Thread.sleep(60000 * 60);
			}

		} catch (Exception e) {
		}

	}// run()

	public void SetnCheck() {
		// System.out.println("SetnChck ");
		for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			boolean AreaCK = false;
			for (int i = 0; i < 8; i++) {
				int castle_id = i + 1;
				if (L1CastleLocation.checkInWarArea(castle_id, pc)) {
					AreaCK = true;
				}
			}

			if (!AreaCK) {
				if (!pc.getMap().isSafetyZone(pc.getLocation())
						&& !pc.isPinkName() && !pc.isGm()) {
					pc.����Ȯ���ʿ���·ιٲٱ�();
					pc
							.sendPackets(new S_ChatPacket(pc, "[�����ڵ�] " + aCode
									+ "  ä��â�� �����ڵ带 �Է��ϼ���.",
									Opcodes.S_OPCODE_NORMALCHAT, 2));
					;
				}
			}// SetnCheck
		}//
	}

	public void Redo() {
	// System.out.println("redo ");
		for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			boolean AreaCK = false;
			for (int i = 0; i < 8; i++) {
				int castle_id = i + 1;
				if (L1CastleLocation.checkInWarArea(castle_id, pc)) {
					AreaCK = true;
				}
			}

			if (!AreaCK) {
				if (!pc.getMap().isSafetyZone(pc.getLocation())
						&& !pc.isPinkName() && !pc.isGm()) {
					if (pc.����Ȯ�����ʿ��ѻ��������Լ�()) {
						pc.sendPackets(new S_ChatPacket(pc, "[�����ڵ�] " + aCode
								+ "  ä��â�� �����ڵ带 �Է��ϼ���.",
								Opcodes.S_OPCODE_NORMALCHAT, 2));
						;

					}
				}
			}
		}
	}

	// Redo

	public void Discon() {
		for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			if (pc.����Ȯ�����ʿ��ѻ��������Լ�()) {
				pc.sendPackets(new S_Disconnect());
			}
		}
	}// Discon
}