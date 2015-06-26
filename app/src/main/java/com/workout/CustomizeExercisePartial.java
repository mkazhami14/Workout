package com.workout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class CustomizeExercisePartial extends DialogFragment{
	
	private Exercise exercise;
	
	private EditText editName;
	private EditText editReps;
	private EditText editSets;
	private EditText editInfo;
    private Switch timer;
	
	private String name;
	private String reps = "";
	private String sets = "";
	private String info = "";
	
	private int position;
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		exercise = bundle.getParcelable(WorkoutObjects.EXER_KEY);
		position = bundle.getInt("position");

        //stop keyboard from popping up
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		AlertDialog.Builder editExercise = new AlertDialog.Builder(getActivity());
    	final View view = getActivity().getLayoutInflater().inflate(R.layout.customize_exercise_partial, null);
    	editExercise.setView(view);
    	
    	editName = (EditText) view.findViewById(R.id.PartialCustomizeNameField);
    	editReps = (EditText) view.findViewById(R.id.PartialCustomizeRepsField);
    	editSets = (EditText) view.findViewById(R.id.PartialCustomizeSetsField);
    	editInfo = (EditText) view.findViewById(R.id.PartialCustomizeInfoField);
        timer = (Switch) view.findViewById(R.id.TimeOrWeight);
        timer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                View repsFieldView = view.findViewById(R.id.PartialCustomizeRepsField);
                View repsTitleView = view.findViewById(R.id.PartialCustomizeRepsTitle);
                if(isChecked) {
                    ((LinearLayout)repsFieldView.getParent()).removeView(repsFieldView);
                    ((LinearLayout)repsTitleView.getParent()).removeView(repsTitleView);
                }
                else {
                    View nameView = view.findViewById(R.id.PartialCustomizeNameField);
                    EditText repsField = new EditText(getActivity());
                    repsField.setId(R.id.PartialCustomizeRepsField);
                    repsField.setRawInputType(InputType.TYPE_CLASS_NUMBER);
                    TextView repsTitle = new TextView(getActivity());
                    repsTitle.setId(R.id.PartialCustomizeRepsTitle);
                    repsTitle.setText("Reps");
                    ((LinearLayout)nameView.getParent()).addView(repsTitle);
                    ((LinearLayout)nameView.getParent()).addView(repsField);
                }
            }
        });
    	editName.setKeyListener(null); // name cannot be changed at this point
        editReps.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        editSets.setRawInputType(InputType.TYPE_CLASS_NUMBER);
    	
    	name = exercise.getName();
		reps = exercise.getReps();
		sets = exercise.getSets();
		info = exercise.getInfo();
		editName.setText(name);
		editReps.setText(reps);
		editSets.setText(sets);
		editInfo.setText(info);

        if(exercise.getTimed().equals("y")) {
            timer.performClick();
        }
		
		editExercise.setTitle("Customize Exercise");
        editExercise.setPositiveButton("Done", new PositiveButtonClickListener());
        editExercise.setNegativeButton("Cancel", new NegativeButtonClickListener());
        
        return editExercise.create();
	}
	
	//defines what a valid rep or set value is
	private boolean isValid(String num) {
		if(num == null) return false;
		int length = num.length();
		for(int i = 0; i < length; i++) {
			char c = num.charAt(i);
			if(c > '9' || c < '0') {
				return false;
			}
		}
		return true;
	}
	
	class PositiveButtonClickListener implements DialogInterface.OnClickListener
    {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
        	sets = editSets.getText().toString();
        	reps = editReps.getText().toString();
        	info = editInfo.getText().toString();

        	//TODO: SHOULDNT EXIT DIALOG IF INVALID
        	if(!timer.isChecked() && (!isValid(reps) || reps.length() == 0)) {
        		Toast toast = Toast.makeText(getActivity(), "Invalid reps!", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
        		return;
        	}
        	if(!isValid(sets) || sets.length() == 0) {
        		Toast toast = Toast.makeText(getActivity(), "Invalid sets!", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
        		return;
        	}
        	if (exercise == null) exercise = new Exercise();
        	exercise.setName(name);
        	exercise.setReps(reps);
        	exercise.setSets(sets);
        	exercise.setInfo(info);
            if(timer.isChecked()) {
                exercise.setTimed("y");
            }
            else {
                exercise.setTimed("n");
            }
        	
        	EditedExercise returnActivity = (EditedExercise) getActivity();
        	returnActivity.onEdit(exercise, position);
        	dialog.dismiss();
        }
    }
    
    class NegativeButtonClickListener implements DialogInterface.OnClickListener
    {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            dialog.dismiss();
        }
        
    }
}
