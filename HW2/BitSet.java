import java.util.*;

public class BitSet {
	private ArrayList<Integer> bset;
	
	public BitSet() {
		bset = new ArrayList<Integer>();
		bset.add(0);
	}
	
	/// 5 == log2(32)
	public static int BitIdxToSlot(int bitidx) { return bitidx >>> 5; }
	/// a mod b == a & (b-1) iff b+1 == 2^n
	public static int BitIdxToBit (int bitidx) { return bitidx  & 31; }
	
	
	public int PopCount() {
		var n = 0;
		for( var i : bset ) {
			n += Integer.bitCount(i);
		}
		return n;
	}
	
	public void Push(int bitidx) {
		final var slot = BitIdxToSlot(bitidx);
		SetBit(bitidx);
		if( Integer.bitCount(bset.get(bset.size()-1)) > 30 ) {
			bset.add(0);
		}
	}
	
	public void ClearBit(int bitidx) {
		final var slot = BitIdxToSlot(bitidx);
		final var bit  = BitIdxToBit(bitidx);
		final var val  = bset.get(slot);
		bset.set(slot, val & ~(1 << bit));
	}
	
	public boolean TestBit(int bitidx) {
		final var slot = BitIdxToSlot(bitidx);
		final var bit  = BitIdxToBit(bitidx);
		final var val  = bset.get(slot);
		return (val & (1 << bit)) > 0;
	}
	
	public void SetBit(int bitidx) {
		final var slot = BitIdxToSlot(bitidx);
		final var bit  = BitIdxToBit(bitidx);
		final var val  = bset.get(slot);
		bset.set(slot, val | (1 << bit));
	}
	
	public void ToggleBit(int bitidx) {
		final var slot = BitIdxToSlot(bitidx);
		final var bit  = BitIdxToBit(bitidx);
		final var val  = bset.get(slot);
		bset.set(slot, val ^ (1 << bit));
	}
}