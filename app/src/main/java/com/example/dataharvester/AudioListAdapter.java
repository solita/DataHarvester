package com.example.dataharvester;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.AudioViewHolder> {
    public static final String EXTRA_MESSAGE = "name";
    private File[] allFiles;
    private com.example.dataharvester.TimeAgo timeAgo;

    private onItemListClick onItemListClick;

    private AudioListAdapter audioListAdapter;
    private List<String> items;

    private Context context;
    public DatabaseHelper databaseHelper = MainActivity.databaseHelper;


    public AudioListAdapter(File[] allFiles, onItemListClick onItemListClick, Context context) {
        this.allFiles = allFiles;
        this.onItemListClick = onItemListClick;
        this.context = context;
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list_item, parent, false);
        timeAgo = new com.example.dataharvester.TimeAgo();
        databaseHelper = new DatabaseHelper(context);
        return new AudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {
        holder.list_title.setText(allFiles[position].getName());
        holder.list_date.setText(timeAgo.getTimeAgo(allFiles[position].lastModified()));
        List<String> all_labels = databaseHelper.getLabels(databaseHelper.getID(allFiles[position].getName()));
        String labels_string ="";
        for (String label : all_labels) {
            labels_string = labels_string + " " + label;
        }
        holder.labels.setText(labels_string);
    }

    @Override
    public int getItemCount() {
        return allFiles.length;
    }

    public class AudioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView list_image;
        private TextView list_title;
        private TextView list_date;
        private TextView labels;


        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);

            list_image = itemView.findViewById(R.id.list_image_view);
            list_title = itemView.findViewById(R.id.list_title);
            list_date = itemView.findViewById(R.id.list_date);
            labels = itemView.findViewById(R.id.labels);


            itemView.setOnClickListener(this);



            itemView.findViewById(R.id.delete_btn).setOnClickListener(view -> {
                int position = getAdapterPosition();
                //System.out.println(position);
                File[] copyArray = new File[allFiles.length - 1];
                System.arraycopy(allFiles, 0, copyArray, 0, position);
                System.arraycopy(allFiles, position + 1, copyArray,
                        position, allFiles.length - position - 1);

                databaseHelper.deleteRecording(databaseHelper.getID(allFiles[position].getName()));

                allFiles[position].delete();
                allFiles = copyArray;
                notifyItemRemoved(position);

            });


            itemView.findViewById(R.id.edit_btn).setOnClickListener(view -> {
                int position = getAdapterPosition();
                File editFile = allFiles[position];
                AlertDialog.Builder editDialog = new AlertDialog.Builder(context);
                editDialog.setTitle("Rename");

                final EditText editName = new EditText(context);
                editName.setInputType(InputType.TYPE_CLASS_TEXT);
                editName.setText(editFile.getName());
                editDialog.setView(editName);

                editDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String newName = editName.getText().toString();
                        if (!newName.isEmpty()) {
                            String path = context.getExternalFilesDir("/").getAbsolutePath();
                            File directory = new File(path);
                            File newFile = new File(directory, newName + ".wav");
                            editFile.renameTo(newFile);
                            list_title.setText(newName + ".wav");


                            //System.out.println(editFile.getName());
                        }
                        else {
                            Toast.makeText(context, "File name cannot be empty", Toast.LENGTH_LONG).show();
                        }
                    }

                });

                editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                editDialog.show();
            });


            /*File file = new File("");

            Retrofit retrofit = NetworkClient.getRetrofit();

            RequestBody requestBody = RequestBody.create(MediaType.parse("recording/*"), file);
            MultipartBody.Part parts = MultipartBody.Part.createFormData("newAudio", file.getName(), requestBody);

            RequestBody recordingData = RequestBody.create(MediaType.parse("text/plain"), "This is a new Image");

            UploadApis uploadApis = retrofit.create(UploadApis.class);


            //Call call = uploadApis.uploadAudio(parts, recordingData);
            retrofit2.Call<RequestBody> call = uploadApis.uploadAudio(parts, recordingData);
            call.enqueue(new Callback<RequestBody>() {
                @Override
                public void onResponse(Call<RequestBody> call, Response<RequestBody> response) {

                }

                @Override
                public void onFailure(Call<RequestBody> call, Throwable t) {

                }
            });*/


        }

        @Override
        public void onClick(View view) {
            onItemListClick.onClickListener(allFiles[getAdapterPosition()], getAdapterPosition());
        }
    }

    public interface onItemListClick {
        void onClickListener(int position);

        void onClickListener(File file, int position);
    }
}

