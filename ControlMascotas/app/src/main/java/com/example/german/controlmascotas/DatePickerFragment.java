package com.example.german.controlmascotas;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.nio.BufferUnderflowException;
import java.util.Calendar;

/**
 * Created by German on 10/04/2016.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

   @Override
   public Dialog onCreateDialog(Bundle savedInstanceState) {
      final Calendar c = Calendar.getInstance();
      int year = c.get(Calendar.YEAR);
      int month = c.get(Calendar.MONTH);
      int day = c.get(Calendar.DAY_OF_MONTH);

      return new DatePickerDialog(getActivity(),this,year,month,day);
   }

   @Override
   public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
      String date = dayOfMonth + "/" + monthOfYear + "/" + year;
      TextView textVFecha = (TextView) getActivity().findViewById(R.id.editTextFechaN);
      textVFecha.setText(date);
   }
}
