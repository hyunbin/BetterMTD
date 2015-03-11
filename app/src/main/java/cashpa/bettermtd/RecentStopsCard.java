package cashpa.bettermtd;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.prototypes.CardWithList;
import it.gmariotti.cardslib.library.prototypes.LinearListView;

/**
 * Created by Hyunbin on 3/8/15.
 */
public class RecentStopsCard extends CardWithList {

    public RecentStopsCard(Context context){
        super(context);
    }

    @Override
    protected CardHeader initCardHeader() {
        //Add Header
        CardHeader header = new CardHeader(getContext(), R.layout.child_bus_stop_header);
        header.setTitle("Recent Stops"); //should use R.string.
        return header;
    }

    @Override
    protected void initCard() {
        setSwipeable(true);
        setEmptyViewViewStubLayoutId(R.layout.base_empty);

        setOnSwipeListener(new OnSwipeListener() {
            @Override
            public void onSwipe(Card card) {
                Toast.makeText(getContext(), "Swipe on " + card.getCardHeader().getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected List<ListObject> initChildren() {
        List<ListObject> mObjects = new ArrayList<ListObject>();

        // Populate the List with OBJECTS
        BusStopObject s1 = new BusStopObject(this);
        s1.busStopObjectName = "NAME 1";
        s1.busStopObjectID = "ID 1";
        s1.setSwipeable(true);
        mObjects.add(s1);

        BusStopObject s2 = new BusStopObject(this);
        s2.busStopObjectName = "NAME 2";
        s2.busStopObjectID = "ID 2";
        s2.setSwipeable(true);
        mObjects.add(s2);

        BusStopObject s3 = new BusStopObject(this);
        s3.busStopObjectName = "NAME 3";
        s3.busStopObjectID = "ID 3";
        s3.setSwipeable(true);
        mObjects.add(s3);

        return mObjects;
    }

    @Override
    public int getChildLayoutId() {
        return R.layout.child_bus_stop;
    }


    @Override
    public View setupChildView(int childPosition, ListObject object, View convertView, ViewGroup parent) {

        //Setup the ui elements inside the item
        TextView textViewName = (TextView) convertView.findViewById(R.id.childBusTextView);

        //Retrieve the values from the object
        BusStopObject stockObject = (BusStopObject) object;
        textViewName.setText(stockObject.busStopObjectName);

        return convertView;
    }

    // This class defines the bus stop object to populate the list
    public class BusStopObject extends DefaultListObject{

        public String busStopObjectName;
        public String busStopObjectID;

        public BusStopObject(Card parentCard) {
            super(parentCard);
            init();
        }

        private void init() {
            //OnClick Listener
            setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(LinearListView parent, View view, int position, ListObject object) {
                    Toast.makeText(getContext(), "Click on " + getObjectId(), Toast.LENGTH_SHORT).show();
                }
            });

            //OnItemSwipeListener
            setOnItemSwipeListener(new OnItemSwipeListener() {
                @Override
                public void onItemSwipe(ListObject object, boolean dismissRight) {
                    Toast.makeText(getContext(), "Swipe on " + object.getObjectId(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public String getObjectId() {
            return busStopObjectName;
        }
    }
}
