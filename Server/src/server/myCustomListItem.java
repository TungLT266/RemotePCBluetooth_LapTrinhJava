package server;
import javax.swing.JLabel;

public class myCustomListItem
{
	public JLabel getLbl()
	{
		return lbl;
	}

	public int getId()
	{
		return id;
	}

	public myCustomListItem(JLabel lbl, int id)
	{
		this.lbl = lbl;
		this.id = id;
	}

	JLabel lbl;
	int id;

}
