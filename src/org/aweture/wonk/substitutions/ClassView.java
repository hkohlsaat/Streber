package org.aweture.wonk.substitutions;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.aweture.wonk.R;
import org.aweture.wonk.models.Class;
import org.aweture.wonk.models.Substitution;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ClassView extends LinearLayout implements Comparator<Substitution> {
	
	private Class currentClass;
	private List<Substitution> substitutions;
	
	private ExpansionCoordinator expansionCoordinator;
	
	private TextView classNameTextView;
	
	private Queue<SubstitutionView> items;
	private Queue<SubstitutionView> itemOverflow;

	public ClassView(Context context) {
		this(context, null);
	}
	public ClassView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	public ClassView(Context context, AttributeSet attrs, int defStyleAttr) {
		this(context, attrs, defStyleAttr, 0);
	}
	public ClassView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		setOrientation(VERTICAL);
		LayoutInflater.from(context).inflate(R.layout.view_class, this, true);

		items = new LinkedList<SubstitutionView>();
		itemOverflow = new LinkedList<SubstitutionView>();
		
		classNameTextView = (TextView) findViewById(R.id.className);
	}
	
	public void setExpansionCoordinatior(ExpansionCoordinator expansionCoordinator) {
		this.expansionCoordinator = expansionCoordinator;
	}
	
	public void setSubstitutions(Class currentClass, List<Substitution> substitutions) {
		this.currentClass = currentClass;
		this.substitutions = substitutions;
		Collections.sort(substitutions, this);

		showClassName();
		recycleAllItems();
		applySubstitutionsToItems();
		showItems();
	}
	
	private void showClassName() {
		String className = currentClass.getName();
		classNameTextView.setText(className);
	}
	
	private void recycleAllItems() {
		SubstitutionView item;
		while ((item = items.poll()) != null) {
			removeView(item);
			itemOverflow.add(item);
		}
	}
	
	private void applySubstitutionsToItems() {
		for (Substitution substitution : substitutions) {
			SubstitutionView nextItem = getUndisplayedSubstitutionView();
			nextItem.setSubstitution(substitution);
			items.add(nextItem);
		}
	}
	
	private SubstitutionView getUndisplayedSubstitutionView() {
		SubstitutionView view = itemOverflow.poll();
		if (view == null) {
			view = new SubstitutionView(getContext());
			view.setExpansionCoordinator(expansionCoordinator);
		}
		return view;
	}
	
	private void showItems() {
		for (SubstitutionView view : items) {
			addView(view);
		}
	}
	
	@Override
	public int compare(Substitution lhs, Substitution rhs) {
		int difference = lhs.getPeriodNumber() - rhs.getPeriodNumber();
		if (difference > 0) {
			return 1;
		} else if (difference < 0) {
			return -1;
		} else {
			return 0;
		}
	}
}
