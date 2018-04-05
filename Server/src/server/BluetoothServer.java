package server;
import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class BluetoothServer
{
	private StreamConnectionNotifier mmService = null;
	private String mmServiceUrl = null;
	private Object WAIT_ForStartListening = new Object();
	RTouchpadMainGui gui;

	boolean mmIslisteningForClients = false;
	private boolean mmIsServerRunning = true;

	public BluetoothServer(String serviceUrl)
	{
		this.mmServiceUrl = serviceUrl;
		gui = Globals.getGlobals().getMainUI();
		serverThread.start();
	}

	public boolean IslisteningForClients()
	{
		synchronized (WAIT_ForStartListening)
		{
			return mmIslisteningForClients;
		}
	}

	public boolean isRunning()
	{
		return mmIsServerRunning;
	}

	Thread serverThread = new Thread(new Runnable()
	{
		@Override
		public void run()
		{
			while (mmIsServerRunning)
			{
				synchronized (WAIT_ForStartListening)
				{
					try
					{
						WAIT_ForStartListening.wait();
					} catch (InterruptedException e)
					{
						e.printStackTrace();
						gui.postLogText(true, "Chương trình đã dừng hoạt đông, hãy khởi động lại chương trình: " + e.getMessage());
						return;
					}
				}
				try
				{
					mmService = (StreamConnectionNotifier) Connector.open(mmServiceUrl);

				} catch (IOException e)
				{
					e.printStackTrace();
					gui.postLogText(true, "Không tìm thấy thiết bị: " + e.getMessage());
					gui.setButtonStatus(false);
					return;
				}

				while (mmIslisteningForClients)
				{
					try
					{
						gui.postLogText(false, "Đang chờ kết nối...");

						StreamConnection connection = mmService.acceptAndOpen();

						gui.postLogText(false, "Yêu cầu gửi đến đã được chấp nhận.");

						BluetoothClient bClient = new BluetoothClient(connection);

						int id = Globals.getGlobals().clientConnected("Client", bClient);
						bClient.setID(id);
						gui.postLogText(false, "ID Client kết nối " + id);

						Thread clientThread = new Thread(bClient);
						clientThread.start();

					} catch (IOException e)
					{
						e.printStackTrace();
						gui.postLogText(false, e.getMessage());
					}
				}

			}

		}
	});

	public void destroy()
	{
		synchronized (WAIT_ForStartListening)
		{
			mmIsServerRunning = false;
			mmIslisteningForClients = false;
			WAIT_ForStartListening.notifyAll();
		}

		try
		{
			mmService.close();

		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void startListening()
	{
		synchronized (WAIT_ForStartListening)
		{
			mmIslisteningForClients = true;
			WAIT_ForStartListening.notifyAll();
		}
	}

	public void stopListening()
	{
		synchronized (WAIT_ForStartListening)
		{
			mmIslisteningForClients = false;
		}

		try
		{
			mmService.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
