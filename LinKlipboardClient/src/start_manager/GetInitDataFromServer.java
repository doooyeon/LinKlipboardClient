package start_manager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

import client_manager.LinKlipboardClient;
import datamanage.ClientInitData;
import server_manager.LinKlipboard;
import transfer_manager.Transfer;
import user_interface.ConnectionPanel;

public class GetInitDataFromServer extends Transfer {

   private ObjectInputStream in;

   private ClientInitData initData;
   
   private ConnectionPanel connectionPanel;

   /** GetTotalHistoryFromServer 생성자 */
   public GetInitDataFromServer(LinKlipboardClient client, ConnectionPanel connectionPanel) {
      super(client);
      this.start();
      this.connectionPanel = connectionPanel;
   }

   /** 서버와의 연결을 위한 소켓과 스트림 설정 */
   @Override
   public void setConnection() {
      try {
         // 소켓 접속 설정
         socket = new Socket(LinKlipboard.SERVER_IP, LinKlipboardClient.getPortNum());
         // 스트림 설정
         in = new ObjectInputStream(socket.getInputStream());
         System.out.println("[GetTotalHistoryFromServer] 연결 설정 끝");

      } catch (UnknownHostException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   /** 열려있는 소켓과 스트림을 모두 닫는다. */
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
      // 서버로부터 Vector<Contents>를 받아온다.
      try {
         System.out.println("[GetTotalHistoryFromServer] Vector<Contents> 수신 전");
         initData = (ClientInitData) in.readObject();
         System.out.println("[GetTotalHistoryFromServer] Vector<Contents> 수신 후");
         //System.out.println("[GetTotalHistoryFromServer]" + historyInServer.get(0).getType());

         // 클라이언트 히스토리에 세팅해준다.
         Vector<String> otherClientsInfo = initData.getClients(); 
         //Vector<Contents> history = initData.getHistory(); 
         
         //LinKlipboardClient.setHistory(history);
         LinKlipboardClient.setOtherClients(otherClientsInfo); 

         System.out.println("[GetTotalHistoryFromServer] 서버가 보낸 접속자 수 " + LinKlipboardClient.getOtherClients().size());
         
         connectionPanel.updateGroupName();
         connectionPanel.updateAccessGroup();
         connectionPanel.repaint();
         
         closeSocket();

         System.out.println("[GetTotalHistoryFromServer] 클라이언트 히스토리 초기화 완료");

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