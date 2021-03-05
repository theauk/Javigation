package bfst21.vector;

import java.util.ArrayList;
import java.util.List;


import bfst21.vector.osm.Way;

public class LongIndexWay {
    List<Way> ways = new ArrayList<>();
    boolean sorted = true;

	public void put(Way way) {
        ways.add(way);
        sorted = false;
	}

	public Way get(long ref) {
        if (!sorted) {
            ways.sort((a, b) -> Long.compare(a.getId(), b.getId()));
            sorted = true;
        }
        int lo = 0;            // nodes.get(lo).getID() <= ref
        int hi = ways.size(); // nodes.get(hi).getID() > ref
        while (lo + 1 < hi) {
            int mi = (lo + hi) / 2;
            if (ways.get(mi).getId() <= ref) {
                lo = mi;
            } else {
                hi = mi;
            }
        }
        Way way = ways.get(lo);
        return way.getId() == ref ? way : null;
	}
}

