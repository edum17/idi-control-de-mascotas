package com.example.german.controlmascotas;

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.DialogFragment;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by German on 01/04/2016.
 */
public class NuevaMascota extends Fragment {

    Context context;

    private final int SELECT_PICTURE = 200;

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1888;
    private String APP_DIRECTORY = "Control de ListaMascotas";
    private String PICTURE_NAME;

    String Path;

    SQLControlador dbconeccion;
    TextView textVFecha;
    Button butGuardarM;
    ImageButton butAnadirImg, butFechaN, butListaT;
    View rootView;

    ImageView img;

    EditText nombre,tipo,fecha,nchip,medicamento,alergia;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.lay_nueva_mascota, container, false);

        context = container.getContext();
        dbconeccion = new SQLControlador(context);
        dbconeccion.abrirBaseDatos();

        img = (ImageView) rootView.findViewById(R.id.image);
        img.setBackgroundResource(R.mipmap.img_def_01);

        textVFecha = (TextView) rootView.findViewById(R.id.textVFechaNac);
        nombre = (EditText) rootView.findViewById(R.id.editTNombreM);
        tipo = (EditText) rootView.findViewById(R.id.editTTipoM);
        fecha = (EditText) rootView.findViewById(R.id.editTextFechaN);
        nchip = (EditText) rootView.findViewById(R.id.editTNumChip);
        medicamento = (EditText) rootView.findViewById(R.id.editTMedicacion);
        alergia = (EditText) rootView.findViewById(R.id.editTAlergia);
        butFechaN = (ImageButton) rootView.findViewById(R.id.butFecha);
        butFechaN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDate();
            }
        });

        butGuardarM = (Button) rootView.findViewById(R.id.butGuardar);
        butGuardarM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(),"Funciona",Toast.LENGTH_SHORT).show();
                addMascotaDB();
            }
        });

        butAnadirImg = (ImageButton) rootView.findViewById(R.id.butImagen);
        butAnadirImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anadirImagen();
            }
        });

        butListaT = (ImageButton) rootView.findViewById(R.id.butListaTipo);
        butListaT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listarTipoAnimales();
            }
        });

        Path = "default";

        return rootView;
    }

    public void addDate() {
        //Toast.makeText(getActivity(), "Funciona", Toast.LENGTH_SHORT).show();
        Calendar mcurrentDate=Calendar.getInstance();
        int mYear=mcurrentDate.get(Calendar.YEAR);
        int mMonth=mcurrentDate.get(Calendar.MONTH);
        int mDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker=new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                // TODO Auto-generated method stub
                String date = selectedday + "/" + (selectedmonth+1) + "/" + selectedyear;
                TextView textVFecha = (TextView) getActivity().findViewById(R.id.editTextFechaN);
                textVFecha.setText(date);
            }
        },mYear, mMonth, mDay);
        mDatePicker.show();
    }

    public void addMascotaDB() {
        //Comprobar si el tipo de animal se ha seleccionado de una lista o se ha añadido manualmente
        if (!nombre.getText().toString().isEmpty() && !tipo.getText().toString().isEmpty() && !fecha.getText().toString().isEmpty() && !nchip.getText().toString().isEmpty()) {

            String med = "No";
            String aler = "No";
            if (!medicamento.getText().toString().equals("")) med = medicamento.getText().toString();
            if (!alergia.getText().toString().equals("")) aler = alergia.getText().toString();

            Mascota m = new Mascota(nombre.getText().toString(), tipo.getText().toString(), fecha.getText().toString(), nchip.getText().toString(), med, aler, Path);

            if (dbconeccion.insertarDatos(m)) {
                //dbconeccion.cerrar();
                Toast.makeText(getActivity(), "Mascota guardada", Toast.LENGTH_SHORT).show();
                clear();
            } else
                Toast.makeText(getActivity(), "Existe una mascota con ese nombre", Toast.LENGTH_SHORT).show();
        }
        else {
            AlertDialog.Builder Adialog = new AlertDialog.Builder(context);
            Adialog.setTitle("Guardar mascota");
            Adialog.setMessage("Para registrar una nueva mascota necesario introducir su nombre, el tipo de animal, la fecha de nacimiento y el número del chip.");
            Adialog.setPositiveButton("Entiendo", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog Alertdialog = Adialog.create();
            Alertdialog.show();
        }
    }

    public void anadirImagen() {
        //Toast.makeText(getActivity(),"Llamo a la camara",Toast.LENGTH_SHORT).show();
        final CharSequence[] items = {"Hacer foto","Galería"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Añadir imagen");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (items[which].equals("Hacer foto")) {
                    openCamera();
                } else if (items[which].equals("Galería")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Seleccionar imagen"), SELECT_PICTURE);
                }
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }


    private void openCamera() {
        File file = new File(Environment.getExternalStorageDirectory(), APP_DIRECTORY);
        file.mkdirs();
        String timeStamp = new SimpleDateFormat("ddmmyyyy_HHmmss").format(new Date());
        PICTURE_NAME = "CM_" + timeStamp + ".jpg";
        String path = Environment.getExternalStorageDirectory() + File.separator + APP_DIRECTORY + File.separator + PICTURE_NAME;
        File newFile = new File(path);

        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); //Mediante este llamada se abirar la camara y captura la imagen
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile)); //Para almacenar imagen o video
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) { //Hacemos foto
            if (resultCode == Activity.RESULT_OK) {
                String dir = Environment.getExternalStorageDirectory() + File.separator + APP_DIRECTORY + File.separator + PICTURE_NAME;
                Bitmap bitmap;
                Path = dir;
                bitmap = BitmapFactory.decodeFile(dir);
                img.setImageBitmap(bitmap);
            }
        }

        else if (requestCode == SELECT_PICTURE) { //Elegimos imagen de la galeria
            if (resultCode == Activity.RESULT_OK) {
                Uri path = data.getData();
                InputStream is;
                try {
                    is = context.getContentResolver().openInputStream(path);
                    BufferedInputStream bis = new BufferedInputStream(is);
                    Bitmap bitmap = BitmapFactory.decodeStream(bis);
                    img.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Cursor c = context.getContentResolver().query(path, null, null, null, null);
                c.moveToFirst();
                int index = c.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                Path = c.getString(index);
                //System.out.println("*************************** Path: " + Path);
            }
        }
    }


    public void listarTipoAnimales() {
        //Toast.makeText(getActivity(), "Listo todos los tipos de animales", Toast.LENGTH_SHORT).show();
        final ArrayList<String> tipoM = dbconeccion.listarTiposAnimales();

        //Creacion de sequencia de items
        final CharSequence[] Animals = tipoM.toArray(new String[tipoM.size()]);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setTitle("Animales");
        dialogBuilder.setItems(Animals, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                String animalSel = Animals[item].toString();  //Selected item in listview
                tipo.setText(animalSel);
            }
        });
        dialogBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        //Creacion del objeto alert dialog via builder
        AlertDialog alertDialogObject = dialogBuilder.create();
        //Mostramos el dialog
        alertDialogObject.show();
    }

    public void clear() {
        img = (ImageView) rootView.findViewById(R.id.image);
        img.setBackgroundResource(R.mipmap.img_def_01);
        nombre.setText(null);
        tipo.setText(null);
        fecha.setText(null);
        nchip.setText(null);
        medicamento.setText(null);
        alergia.setText(null);
    }
}
