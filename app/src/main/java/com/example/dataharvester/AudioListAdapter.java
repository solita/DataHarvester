package com.example.dataharvester;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        private MediaPlayer mp;


        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);

            list_image = itemView.findViewById(R.id.list_image_view);
            list_title = itemView.findViewById(R.id.list_title);
            list_date = itemView.findViewById(R.id.list_date);
            labels = itemView.findViewById(R.id.labels);


            itemView.findViewById(R.id.analysis_btn).setVisibility(View.GONE);
            itemView.setOnClickListener(this);


            list_image.setOnClickListener( view -> {
                int position = getAdapterPosition();

                File recordingFile = allFiles[position];
                mp = MediaPlayer.create(view.getContext(), Uri.fromFile(recordingFile));
                mp.start();
            });


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

                Intent intent = (new Intent(HistoryActivity.audioListAdapter.context, EditLabelsActivity.class));
                intent.putExtra(EXTRA_MESSAGE,allFiles[position].getName());
                HistoryActivity.audioListAdapter.context.startActivity(intent);




            });
            /*
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
            */
            // TODO: check if the file has been uploaded and analysed and pick the correct button
            itemView.findViewById(R.id.upload_btn).setOnClickListener(view -> {
                int position = getAdapterPosition();

                File recordingFile = allFiles[position];
                RequestBody reqBody = RequestBody.create(MediaType.parse("multipart/form-file"), recordingFile);
                MultipartBody.Part partAudio = MultipartBody.Part.createFormData("filename", recordingFile.getName(), reqBody);
                UploadApis api = NetworkClient.getInstance().getAPI();
                Call<ResponseBody> upload = api.uploadFiles(partAudio);

                upload.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        if(response.isSuccessful()) {

                            uploadLabel(recordingFile);

                            // After uploading the file, the upload button is not needed so it need to hide.
                            itemView.findViewById(R.id.upload_btn).setVisibility(View.GONE);
                            itemView.findViewById(R.id.analysis_btn).setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(context, "Upload failed", Toast.LENGTH_SHORT).show();
                        System.out.println("failure");
                    }
                });
            });

            //listener for analysis button, starts analysis activity with the id of the selected file
            itemView.findViewById(R.id.analysis_btn).setOnClickListener(view -> {
                //get current file from list of all files
                File file = allFiles[getAdapterPosition()];
                //get current file id
                int id = databaseHelper.getID(file.getName());

                Intent showAnalysis = new Intent(HistoryActivity.audioListAdapter.context, AnalysisActivity.class);

                //pass id as parameter
                showAnalysis.putExtra("id", id);
                HistoryActivity.audioListAdapter.context.startActivity(showAnalysis);
            });

        }

        @Override
        public void onClick(View view) {
            onItemListClick.onClickListener(allFiles[getAdapterPosition()], getAdapterPosition());
        }
    }

    private void uploadLabel(File file) {

        String fileName = file.getName();
        List<String>  labelList = databaseHelper.getLabels(databaseHelper.getID(fileName));
        String allLabel = "";
        for(int i = 0; i < labelList.size(); ++i) {
            allLabel += labelList.get(i);

            if (i < labelList.size() - 1) {
                allLabel += ",";
            }
        }
        System.out.println(allLabel);

        UploadApis api = NetworkClient.getInstance().getAPI();
        Call<ResponseBody> upload = api.uploadLabel(fileName, allLabel);

        upload.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()) {

                    JSONObject jsonObject = null;

                    try {

                        jsonObject = new JSONObject(response.body().string());
                        String json = jsonObject.toString();

                        int ID = databaseHelper.getID(fileName);
                        databaseHelper.deleteJson(ID);
                        databaseHelper.addJSON(json, ID);

                        //System.out.println(databaseHelper.getJSON(ID));
                        //System.out.println(response.body().string());
                        //System.out.println(jsonObject);
                        //System.out.println(json);
                        //System.out.println(jsonObject.getString("filename"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(context, "Upload succeed", Toast.LENGTH_SHORT).show();
                    System.out.println("label uploaded");
                    //System.out.println(response.toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "Upload failed", Toast.LENGTH_SHORT).show();
                System.out.println("failure");
            }
        });
    }

    public interface onItemListClick {
        void onClickListener(int position);

        void onClickListener(File file, int position);
    }
}

