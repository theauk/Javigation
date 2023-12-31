package bfst21.data_structures;

import bfst21.Osm_Elements.Element;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Binary search tree adapted from Troels Bjerre Lund.
 * Orders the search tree based on elements id.
 * Sorts the tree when the get method is called.
 *
 * @param <Value> Value extends Element class.
 */
public class BinarySearchTree<Value extends Element> {
    private final List<Value> values = new ArrayList<>();
    private boolean sorted = true;

    public void put(Value val) {
        values.add(val);
        sorted = false;
    }

    public Value get(long key) {
        if (!sorted) {
            values.sort(Comparator.comparingLong(Value::getId));
            sorted = true;
        }
        int lo = 0;
        int hi = values.size();
        while (lo + 1 < hi) {
            int mid = (lo + hi) / 2;

            int compare = Long.compare(key, (values.get(mid).getId()));
            if (compare < 0) {
                hi = mid;
            } else {
                lo = mid;
            }
        }
        Value val = values.get(lo);
        return val.getId() == key ? val : null;
    }
}

