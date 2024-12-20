package com.example.photos;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.photos.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

//I am getting the image in and storing data in db, now create a getData to get the data from the database and send it to the adapter.
// reminder you are only gettind the image and separately adding it to db  then you need to follow the eralier assingment which is stored in downlaods under Goup7_Homework05\HW05Mariann

public class ProfileFragment extends Fragment  implements ProfileRecycAdapter.onDeleteClick, ProfileRecycAdapter.onCommentClick{

    ArrayList<String> imagelist= new ArrayList<>();
    ArrayList<Photo> photos = new ArrayList<>();
    RecyclerView recyclerView;

    ProfileRecycAdapter adapter;
    LinearLayoutManager layoutManager;
    FirebaseAuth mAuth;
    Button selectImage;
    Button uploadImage;
    TextView name;
    TextView email;
    Button refresh;
    ImageView imageView;
    private Uri filePath;
    String docId;
    // request code
    private final int PICK_IMAGE_REQUEST = 22;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseDatabase db;
    DatabaseReference databaseReference;
    FirebaseFirestore dbi = FirebaseFirestore.getInstance();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_OID = "OID";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String ownerID;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public ProfileFragment(String uid){
        ownerID = uid;
    }

/*    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/

    public static Fragment newInstance(String ownerID) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_OID, ownerID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ownerID = getArguments().getString(ARG_OID);
           // mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    FragmentProfileBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater,container,false);
        return  binding.getRoot();


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Profile");
        recyclerView = binding.recycViewx;
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        updateList();
        getData();
        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();
        adapter = new ProfileRecycAdapter(photos,mAuth.getUid(),this, this, this, (ProfileRecycAdapter.onCommentClick) this);


        name = binding.textViewName;
        email = binding.textViewEmail;
        name.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        imageView = binding.imageView1;
        selectImage = binding.buttonAddPhoto;
        uploadImage = binding.buttonUpload;
        //refresh = binding.buttonRefresh;
        uploadImage.setVisibility(View.INVISIBLE);

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("images");

        db = FirebaseDatabase.getInstance();
        //databaseReference = db.getReference("images");



        super.onViewCreated(view, savedInstanceState);

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SelectImage();
               // uploadImage.setVisibility(View.VISIBLE);
            }
        });

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UploadImage();
                adapter.notifyDataSetChanged();
                //mlistener.goToprofiel();

            }
        });


    }
    void updateList(){
        StorageReference listRef = FirebaseStorage.getInstance().getReference().child("images/images");
        listRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference file: listResult.getItems()){
                    file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imagelist.add(uri.toString());
                            Log.d("demo", "onSuccess: itemvalue "+ uri.toString());
                            adapter.notifyDataSetChanged();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            recyclerView.setAdapter(adapter);

                        }
                    });
                }
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof ProfileIlistener){
            mlistener = (ProfileIlistener) context;
        }
    }

    ProfileIlistener mlistener;

    @Override
    public void onDeleteClick(int position) {
        deletePhoto(position);
    }

    @Override
    public void onCommentClick(int position, String docId, String currentUserId, String uri) {
        commentPhoto(position,docId,currentUserId, uri);

    }




    interface ProfileIlistener{
        void goToProfile();
        void goToComment(String docId, String currentUid, String uri);

    }
    // Select Image method
    private void SelectImage()
    {

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }
    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode,
                                 Intent data)
    {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();


                // Setting image on image view using Bitmap
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                              getActivity().getContentResolver(),
                                filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(bitmap);
            uploadImage.setVisibility(View.VISIBLE);






        }
    }


    // UploadImage method
    private void UploadImage() {
        Photo photo = new Photo();

        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child(
                            "images/"
                                    + UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot) {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), "Image Uploaded!!",Toast.LENGTH_SHORT).show();
                                    imagelist.add(filePath.toString());



                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            //hash map to sore data in db
                                            HashMap<String, Object> kep = new HashMap<>();
                                            kep.put("photoOwner",mAuth.getCurrentUser().getDisplayName());
                                            kep.put("uid",mAuth.getUid());
                                            kep.put("uri", uri.toString());

                                            Log.d("demo", "onSuccess: "+ kep);


                                            dbi.collection("images").document().set(kep).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Log.d("demo", "onSuccess: Forum Successfully posted ");
                                                    getData();
                                                }
                                            })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.d("demo", "onFailure: Fail"+ e.toString());
                                                        }
                                                    });
                                           /*
                                            setData(photo.getDocId(),mAuth.getUid(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),uri.toString());*/
                                            adapter.notifyDataSetChanged();
                                        }
                                    });


                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(getContext(),
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int) progress + "%");
                                    uploadImage.setVisibility(View.INVISIBLE);
                                }
                            });
        }

    }
    void getData(){
        dbi.collection("images").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        photos.clear();
                        for(QueryDocumentSnapshot document : queryDocumentSnapshots){
                            docId = document.getId();
                            Log.d("demo", "onSuccess: doc id is "+ docId);

                            // public Photo(String docId,String photoOwner, String uri, String getPhotoOwnerId) {
                            if(ownerID.equals(document.getString("uid"))){
                           // if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(document.getString("uid"))) {
                                photos.add(new Photo(docId, document.getString("photoOwner"), document.getString("uri"), document.getString("uid")));
                            }
                        }
                        for (Photo forum : photos) {
                            Log.d("demo", "onEvent: forum info " + forum);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("demo", "onFailure: "+e);
            }
        });

    }
    void commentPhoto(int p, String docId, String uid, String uri){
        mlistener.goToComment(docId, uid, uri);
    }
    void deletePhoto(int p){

        String photoID = photos.get(p).getDocId();
        Log.d("demo", "deletePhoto: the doc id in delete is"+ photoID);

        Map<String, Object> photoN = new HashMap<>();
        photoN.put("uid", FieldValue.delete());
        photoN.put("photoOwner", FieldValue.delete());
        photoN.put("uri", FieldValue.delete());
        dbi.collection("images").document(photoID).update(photoN).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("demo", "onSuccess: Success in deleting fields");

            }
        });
        dbi.collection("images").document(photoID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("demo", "onSuccess: Deleted successfully");
                        Log.d("demo", "onSuccess: ");
                        getData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("demo", "onFailure: Error Deleting Document "+e);

                    }
                });
        adapter.notifyDataSetChanged();
    }

    private void setData(String images, String uId, String photoOwner, String uri){

        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Date c = Calendar.getInstance().getTime();
        Log.d("demo", "setData: creator is  "+ mAuth.getCurrentUser());

        HashMap<String, Object> photo = new HashMap<>();

        Log.d("demo", "setData: title is "+ uId);
        Log.d("demo", "setData: desc is "+ photoOwner);
        Log.d("demo", "setData: current user "+uri);
        photo.put("uid", uId);
        photo.put("photoOwner", photoOwner);
        photo.put("uri", uri);

        db.collection("images").document().set(photo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("demo", "onSuccess: Image Info Successfuly posted ");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("demo", "onFailure: Fail"+ e.toString());
                    }
                });
    }

}



class ProfileRecycAdapter extends RecyclerView.Adapter<ProfileRecycAdapter.ProfileViewHolder>{

    private ArrayList<Photo> photos;
    private onDeleteClick monDeleteListener;
    private onCommentClick monCommentListener;
    String currentUserId;

    public ProfileRecycAdapter(ArrayList<Photo> photos, String currentUserId,ProfileFragment deleteListener, onDeleteClick onDeleteListener, ProfileFragment commentListener, onCommentClick monCommentListener) {

        this.photos = photos;
        this.currentUserId = currentUserId;
        this.monDeleteListener = onDeleteListener;
        this.monCommentListener = monCommentListener;

    }



    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_item, parent,false);
        ProfileViewHolder profileViewHolder = new ProfileViewHolder(view, monDeleteListener, monCommentListener);
        return profileViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileRecycAdapter.ProfileViewHolder holder, int position) {

        Photo photo = photos.get(position);

        //Glide.with(holder.imageView.getContext()).load(photos.get(position)).into(holder.imageView);

        holder.imageViewIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("demo", "onClick: getDoc id "+ photo.getDocId());
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                ProfileFragment profileFragment = new ProfileFragment();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.containerView,  CommentFragment.newInstance(photo.getDocId(), currentUserId, photo.getUri()))
                        .addToBackStack(null)
                        .commit();

            }
        });

        Glide.with(holder.imageViewIm.getContext()).load(Uri.parse(photos.get(position).getUri())).into(holder.imageViewIm);

        holder.photo = photo;

        if(holder!=null) {
            if (photo.getUid().equals(currentUserId)) {
                holder.imageViewTrash.setImageResource(R.drawable.rubbish_bin);
                holder.imageViewTrash.setVisibility(View.VISIBLE);
            } else {
                holder.imageViewTrash.setVisibility(View.INVISIBLE);
            }
        }

      //  holder.imageViewTrash.setImageResource(R.drawable.rubbish_bin);

    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public class ProfileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        ImageView imageViewTrash;
        ImageView imageViewLike;
        Button buttonComment;
        ImageView imageViewIm;
        onDeleteClick onDeleteListener;
        onCommentClick onCommentListener;
        Photo photo;

        public ProfileViewHolder(@NonNull View itemView, onDeleteClick onDeleteListener, onCommentClick onCommentListener) {
            super(itemView);
            imageViewIm = itemView.findViewById(R.id.imageViewImage);
            itemView.setOnClickListener(this);
            this.onDeleteListener = onDeleteListener;
            this.onCommentListener = onCommentListener;
            buttonComment = itemView.findViewById(R.id.buttonComment);
            buttonComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("demo", "onClick: you clicked on comment for "+ getAdapterPosition());
                    onCommentListener.onCommentClick(getAdapterPosition(), photo.getDocId(), currentUserId, photo.getUri());
                }
            });

            imageViewTrash =itemView.findViewById(R.id.imageViewTR);
            itemView.setOnClickListener(this);
            if(imageViewTrash!= null){
                imageViewTrash.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Log.d("demo", "onClick: you clicked on delete");
                        onDeleteListener.onDeleteClick(getAdapterPosition());
                    }
                });
            }
        }

        @Override
        public void onClick(View view) {

        }
    }
    interface onDeleteClick{
        void onDeleteClick(int position);
    }
    interface onCommentClick{
        void onCommentClick(int position, String docId, String currentUserId, String uri);
    }
}