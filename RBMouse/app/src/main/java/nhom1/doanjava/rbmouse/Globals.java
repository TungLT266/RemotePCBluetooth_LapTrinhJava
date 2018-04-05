package nhom1.doanjava.rbmouse;

import java.util.Hashtable;

public final class Globals
{

    private static Globals mmGlobal = new Globals();

    Hashtable<Integer, Object> mmObjectsToTransfer = new Hashtable<Integer, Object>();

    static int mmSampleIdGenerator = Integer.MIN_VALUE;

    private Globals()
    {
    }

    public static Globals getGlobal()
    {
        return mmGlobal;
    }

    private int nextID()
    {
        return mmSampleIdGenerator++;
    }

    public int putTransferObject(Object obj)
    {
        int id = nextID();
        mmObjectsToTransfer.put(id, obj);
        return id;
    }

    public Object getTransferedObject(int id)
    {
        return mmObjectsToTransfer.remove(id);
    }
}