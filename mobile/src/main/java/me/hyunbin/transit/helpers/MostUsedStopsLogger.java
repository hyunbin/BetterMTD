package me.hyunbin.transit.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;
import me.hyunbin.transit.models.SimpleCountingStop;

/**
 * Created by Hyunbin on 2/25/17.
 */

public class MostUsedStopsLogger {
  private final static String ITEM_NAME = "stop_counts";

  private Map<SimpleCountingStop, Integer> mStopCountDatabase;

  public MostUsedStopsLogger() {
    mStopCountDatabase = Paper.book().read(ITEM_NAME, new HashMap<SimpleCountingStop, Integer>());
  }

  public void increment(SimpleCountingStop stop) {
    int k = mStopCountDatabase.get(stop) != null ? mStopCountDatabase.get(stop) : 0;
    mStopCountDatabase.put(stop, k + 1);
  }

  public void commit() {
    Paper.book().write(ITEM_NAME, mStopCountDatabase);
  }

  public List<SimpleCountingStop> getMostFrequentStops() {
    List<Map.Entry<SimpleCountingStop, Integer>> entryList =
        new ArrayList<>(mStopCountDatabase.entrySet());
    Collections.sort(entryList, new Comparator<Map.Entry<SimpleCountingStop, Integer>>() {
      @Override
      public int compare(
          Map.Entry<SimpleCountingStop, Integer> t1,
          Map.Entry<SimpleCountingStop, Integer> t2) {
        int v1 = t1.getValue() != null ? t1.getValue() : 0;
        int v2 = t2.getValue() != null ? t2.getValue() : 0;
        return v1 - v2;
      }
    });
    Collections.reverse(entryList);
    List<SimpleCountingStop> simpleList = convertEntryToSimpleList(entryList);
    if (simpleList.size() < 5) {
      return simpleList;
    } else {
      return simpleList.subList(0, 5);
    }
  }

  private List<SimpleCountingStop> convertEntryToSimpleList(
      List<Map.Entry<SimpleCountingStop, Integer>> list) {
    List<SimpleCountingStop> simpleList = new ArrayList<>();
    for (Map.Entry<SimpleCountingStop, Integer> item : list) {
      simpleList.add(item.getKey());
    }
    return simpleList;
  }
}
