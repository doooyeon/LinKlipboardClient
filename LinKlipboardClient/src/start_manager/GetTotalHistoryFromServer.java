package start_manager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

import client_manager.LinKlipboardClient;
import contents.Contents;
import server_manager.LinKlipboard;
import transfer_manager.Transfer;

public class GetTotalHistoryFromServer extends Transfer {

	private ObjectInputStream in;

	private Vector<Contents> historyInServer; // �����κ��� ���� Vector<Contents>

	/** GetTotalHistoryFromServer ������ */
	public GetTotalHistoryFromServer() {
		super();
		this.start();
	}

	/** �������� ������ ���� ���ϰ� ��Ʈ�� ���� */
	@Override
	public void setConnection() {
		try {
			// ���� ���� ����
			socket = new Socket(LinKlipboard.SERVER_IP, LinKlipboard.FTP_PORT);
			// ��Ʈ�� ����
			in = new ObjectInputStream(socket.getInputStream());
			System.out.println("[GetTotalHistoryFromServer] ���� ���� ��");

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** �����ִ� ���ϰ� ��Ʈ���� ��� �ݴ´�. */
	@Override
	public void closeSocket() {
		try {
			in.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		setConnection();
		// �����κ��� Vector<Contents>�� �޾ƿ´�.
		try {
			historyInServer = (Vector<Contents>) in.readObject();

			// Ŭ���̾�Ʈ �����丮�� �������ش�.
			LinKlipboardClient.setHistory(historyInServer);

			closeSocket();

			System.out.println("[GetTotalHistoryFromServer] Ŭ���̾�Ʈ �����丮 �ʱ�ȭ �Ϸ�");

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			closeSocket();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			closeSocket();
			return;
		}
	}
}