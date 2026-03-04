// simply records memory usage

public class MemoryRecorder {
    private final long _mb = 1024L * 1024L;
    private Runtime _runtime;

    public MemoryRecorder() {
        _runtime = Runtime.getRuntime();
    }

    public long[] getMemory() {
        long[] values = new long[4];

        long totalMemory = _runtime.totalMemory();
        long freeMemory = _runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = _runtime.maxMemory();

        long umMb = usedMemory / _mb, mmMb = maxMemory / _mb;
        values[0] = umMb;
        values[1] = mmMb;
        values[2] = freeMemory;
        values[3] = totalMemory;

        return values;
    }
}
