package com.example.german.controlmascotas;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by German on 07/05/2016.
 */
public class ListViewAdapterDiasAgenda extends BaseAdapter{

    Context context;
    ArrayList<Cita> cita;
    LayoutInflater inflater;
    SQLControlador dbconeccion;


    public ListViewAdapterDiasAgenda(Context context, ArrayList<Cita> cita) {
        this.context = context;
        this.cita = cita;
        dbconeccion = new SQLControlador(context);
        dbconeccion.abrirBaseDatos();
    }

    @Override
    public int getCount() {
        return cita.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    private boolean existeCitaFecha(ArrayList<Cita> listaDiasAgenda, ArrayList<Cita> listaCitasDia) {
        boolean b = false;
        int i = 0;
        while (i < listaDiasAgenda.size() && !b) {
            int j = 0;
            while (j < listaCitasDia.size() && !b) {
                if (listaDiasAgenda.get(i).getFecha().toString().equals(listaCitasDia.get(j).getFecha().toString())) b = true;
                else ++j;
            }
            ++i;
        }
        return b;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        final TextView diaSemana;
        final ListView eventosDia;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View itemView = inflater.inflate(R.layout.formato_fila_dias_agenda,parent,false);

        diaSemana = (TextView) itemView.findViewById(R.id.fila_dia_diaSemana);
        eventosDia = (ListView) itemView.findViewById(R.id.listViewEventosDia);

        diaSemana.setText(cita.get(position).getNomDiaFecha());

        //System.out.println("*************************** FECHA ADAPTER: " + cita.get(position).getFecha());
        final String fecha = cita.get(position).getFecha();
        //if (existeCitaFecha(dbconeccion.listarDiasAgenda(),dbconeccion.listarCitasDia(fecha))) diaSemana.setText(cita.get(position).getNomDiaFecha());
        //else diaSemana.setText(null);

        final ListViewAdapterCitaDia[] adapterHoraEvento = {new ListViewAdapterCitaDia(context, dbconeccion.listarCitasDia(fecha))};
        adapterHoraEvento[0].notifyDataSetChanged();
        eventosDia.setAdapter(adapterHoraEvento[0]);
        ListViewSinScroll.setListViewHeightBasedOnItems(eventosDia);
        eventosDia.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                final CharSequence[] items = {"Eliminar cita", "Modificar cita"};
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Cita");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (items[which].equals("Eliminar cita")) {
                            String nomC = adapterHoraEvento[0].getItemNameC(position);
                            String horaIni = adapterHoraEvento[0].getItemHoraIni(position);
                            dbconeccion.eliminarCita(nomC, fecha, horaIni);

                            //¡¡¡¡¡ERROR!!!!!
                            //===============
                            //No funciona al cien por cien
                            if (existeCitaFecha(dbconeccion.listarDiasAgenda(), dbconeccion.listarCitasDia(fecha)))
                                diaSemana.setText(cita.get(position).getNomDiaFecha());

                            else diaSemana.setText(null);
                            final ListViewAdapterCitaDia[] adapterHoraEvento = {new ListViewAdapterCitaDia(context, dbconeccion.listarCitasDia(fecha))};
                            adapterHoraEvento[0].notifyDataSetChanged();
                            eventosDia.setAdapter(adapterHoraEvento[0]);
                            ListViewSinScroll.setListViewHeightBasedOnItems(eventosDia);


                            Toast.makeText(context, "La cita ha sido eliminada", Toast.LENGTH_SHORT).show();
                        } else if (items[which].equals("Modificar cita")) {
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
        });
        return itemView;
    }
}
