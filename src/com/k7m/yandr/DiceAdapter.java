package com.k7m.yandr;


import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.k7m.yandr.R;

public class DiceAdapter extends BaseAdapter{

	private Context mContext;

	public DiceAdapter(Context context) {
		mContext = context;		
	}
	public DiceAdapter(Context context, ArrayList<Dice> list) {
		mContext = context;
		diceList = list;
	}
	public int getCount() {
		return diceList.size();
	}
	public void clearDice() {
		diceList.clear();
	}
	public void removeDice(int position) {
		diceList.remove(position);
	}
	public Integer getDiceIconFromPosition(int position) {
		return mThumbIds[diceList.get(position).getSides()];
	}	
	public Integer getdDiceIconFromSides(int sides) {
		return mThumbIds[sides];
	}
	public Object getItem(int position) {
		return diceList.get(position); 
	}
	public long getItemId(int position) {
		return diceList.get(position).getSides();
	}    
	public View getView(int position, View convertView, ViewGroup parent) {
		Dice currentDie = diceList.get(position);
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout;
		TextView t1,t2,t3,t4,t5;
		ImageView imageview;
		if (convertView == null) {  // if it's not recycled(ie. null/incomplete), initialise some attributes    		
			layout = new View(mContext);
		} else {
			layout = convertView;    			    
		}
		//now layout is a valid View object, we can start playing with it
		layout = inflater.inflate(R.layout.dicelayout3, null, true); 
		t1 = (TextView)layout.findViewById(R.id.dice_multi);
		t1.setGravity(Gravity.CENTER_VERTICAL);
		t2 = (TextView)layout.findViewById(R.id.dice_mod);
		t2.setGravity(Gravity.CENTER_VERTICAL);
		t3 = (TextView)layout.findViewById(R.id.dice_result1);
		t3.setGravity(Gravity.CENTER_HORIZONTAL);
		t4 = (TextView)layout.findViewById(R.id.dice_result2);
		t4.setGravity(Gravity.CENTER_HORIZONTAL);
		t5 = (TextView)layout.findViewById(R.id.dice_result);
		t5.setGravity(Gravity.CENTER_HORIZONTAL);
		imageview = (ImageView)layout.findViewById(R.id.dice_icon);
		String multi = ((Integer)currentDie.getMultiplier()).toString();
		String mod = ((Integer)currentDie.getModifier()).toString();
		if (currentDie.getMultiplier() > 1) {
			t1.setText(multi);	
		}
		if (currentDie.getModifier() != 0) {
			t2.setText(mod);
		}	
		//Draw the results		
		if (currentDie.getResult() >0) {
			Integer result;
			if (sumTotals = true) {
				result = currentDie.getTotal();
			} else {
				result = currentDie.getResult();
			}
			if (result >= 100){
				t5.setTextSize(Float.valueOf(40));	
				t5.setGravity(16);
			}
			t5.setTextColor(Color.RED);
			t5.setText(result.toString()); 	
		}

		imageview.setImageResource(mThumbIds[currentDie.getSides()]);
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
	private ArrayList<Dice> diceList;
	private Boolean sumTotals;
}
