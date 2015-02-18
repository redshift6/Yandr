package com.k7m.yandr;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class DiceAdapter extends BaseAdapter {

	private Context mContext;

	public DiceAdapter(Context context) {
		mContext = context;		
	}
	public DiceAdapter(Context context, ArrayList<SimpleDice> list) {
		mContext = context;
		diceList = list;
	}
	public int getCount() {
		return diceList.size();
	}
	public Integer getDiceIconFromPosition(int position) {
		return mThumbIds[diceList.get(position).getSides()];
	}
	public Object getItem(int position) {
		return diceList.get(position); 
	}
	public long getItemId(int position) {
		return diceList.get(position).getSides();
	}    
	public View getView(int position, View convertView, ViewGroup parent) {
		SimpleDice currentDie = diceList.get(position);
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout;

		ImageView imageview;
		if (convertView == null) {  // if it's not recycled(ie. null/incomplete), initialise some attributes    		
			layout = new View(mContext);
		} else {
			layout = convertView;    			    
		}

		if (currentDie instanceof D20Dice) {
			//currentDie = (D20Dice) currentDie;
			TextView t1,t2,t3,t4,t5;
			//now layout is a valid View object, we can start playing with it
			layout = inflater.inflate(R.layout.dicelayout3, null, true);
			t1 = (TextView)layout.findViewById(R.id.dice_multi);
			t2 = (TextView)layout.findViewById(R.id.dice_mod);
			t3 = (TextView)layout.findViewById(R.id.dice_result1);
			t4 = (TextView)layout.findViewById(R.id.dice_result2);
			t5 = (TextView)layout.findViewById(R.id.dice_result);
			imageview = (ImageView)layout.findViewById(R.id.dice_icon);
			String multi = (currentDie.getMultiplier()).toString();
			String mod = (currentDie.getModifier()).toString();
			String name = currentDie.getName();
			if (currentDie.getMultiplier() > 1) {
				t1.setText(multi);
			}
			if (currentDie.getModifier() != 0) {
				t2.setText(mod);
			}
			if (name != null && name != "") {
				t3.setText(name);
			}
			//Draw the results
			Integer value = Integer.valueOf(currentDie.getResult());
			if (value >0) {
				String result;
				if (sumTotals) {
					result = currentDie.getTotal();
				} else {
					result = currentDie.getResult();
				}
				t5.setTextColor(Color.RED);
				t5.setText(result);
			}
			imageview.setImageResource(mThumbIds[currentDie.getSides()]);
			return layout;
		} else if (currentDie instanceof ColourDice) {
			layout = inflater.inflate(R.layout.colourdice, null, true);
			imageview = (ImageView)layout.findViewById(R.id.dice_icon);
			if (currentDie.getResult() != null) {
				imageview.setImageDrawable(new ColorDrawable(Color.parseColor(currentDie.getResult())));
			} else imageview.setImageResource(R.drawable.dttr);
			return layout;
		}
		//Its something else, and we just don't know how to handle that
		return layout;
	}
	public void setSumTotals(Boolean totals) {
		sumTotals = totals;
	}
	private Integer[] mThumbIds = { R.drawable.ic_star_w,
			R.drawable.d1s, R.drawable.d2,
			R.drawable.d3, R.drawable.d4g,
			R.drawable.ic_star_w, R.drawable.d6g,
			R.drawable.ic_star_w, R.drawable.d8g,
			R.drawable.ic_star_w, R.drawable.d10g,
			R.drawable.ic_star_w, R.drawable.d12g,
			R.drawable.ic_star_w, R.drawable.d14g,
			R.drawable.ic_star_w, R.drawable.d16g,
			R.drawable.ic_star_w, R.drawable.d18g,
			R.drawable.ic_star_w, R.drawable.d20g            
	};
	private ArrayList<SimpleDice> diceList;
	private Boolean sumTotals = false;

}
