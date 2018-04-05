package server;
import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.io.StreamConnection;

public class BluetoothClient implements Runnable
{
	InputStream mmInputStream;
	boolean mmIsRunning = true;
	int mmMousePointerSpeed = 1;
	Point mmDeviceScreenDimensions;
	int mmId = -1;

	public BluetoothClient(StreamConnection connection) throws IOException
	{
		this.mmInputStream = connection.openInputStream();
	}

	@Override
	public void run()
	{
		Robot xxRobot = null;
		try
		{
			GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice gDev = gEnv.getDefaultScreenDevice();

			xxRobot = new Robot(gDev);
		} catch (AWTException e)
		{
			e.printStackTrace();
			disconnect();
			return;
		}

		DataInputStream xxScanner = new DataInputStream(mmInputStream);
		int incommingCommand = -1, incommingCoordinateX = 0, incommingCoordinateY = 0;

		while (mmIsRunning)
		{
			try
			{
				incommingCommand = xxScanner.readInt();
				Point currentMouseLocation = MouseInfo.getPointerInfo().getLocation();

				switch (incommingCommand)
				{
				case BlueMoProtocol.ACTION_MOVE:
					incommingCoordinateX = xxScanner.readInt();
					incommingCoordinateY = xxScanner.readInt();
					xxRobot.mouseMove(currentMouseLocation.x - incommingCoordinateX, currentMouseLocation.y - incommingCoordinateY);
					break;

				case BlueMoProtocol.ACTION_SINLE_TAPP:
					xxRobot.mousePress(MouseEvent.BUTTON1_MASK);
					xxRobot.mouseRelease(MouseEvent.BUTTON1_MASK);
					break;

				case BlueMoProtocol.ACTION_MOUSE_LEFT_BUTTON_DOWN:
					xxRobot.mousePress(MouseEvent.BUTTON1_MASK);
					break;

				case BlueMoProtocol.ACTION_MOUSE_LEFT_BUTTON_UP:
					xxRobot.mouseRelease(MouseEvent.BUTTON1_MASK);
					break;

				case BlueMoProtocol.ACTION_MOUSE_RIGHT_BUTTON_DOWN:
					xxRobot.mousePress(MouseEvent.BUTTON3_MASK);
					break;

				case BlueMoProtocol.ACTION_MOUSE_RIGHT_BUTTON_UP:
					xxRobot.mouseRelease(MouseEvent.BUTTON3_MASK);
					break;
				}

			} catch (IOException e)
			{
				disconnect();
				return;
			}

		}
		disconnect();
	}

	public void setID(int id)
	{
		this.mmId = id;
	}

	public void disconnect()
	{
		mmIsRunning = false;
		try
		{
			Globals g = Globals.getGlobals();
			g.getMainUI().postLogText(false, "ID Client dừng kết nối: " + mmId);
			g.clientDisconnected(mmId);
			mmInputStream.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
