package com.example.photos;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.photos.databinding.FragmentCommentBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CommentFragment extends Fragment implements CommentsRecyclerAdapter.onDeleteClick{

    private FirebaseAuth mAuth;
    RecyclerView commentsRecyclerView;
    CommentsRecyclerAdapter adapter;
    LinearLayoutManager layoutManager;
    ArrayList<Comment> comments = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String commentId;
    TextView imageOwner;
    Button postBtn;
    EditText nComment;
    ImageView imageDisp;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_DocID = "ARG_DocID";

    private static final String ARG_USERID = "ARG_USERID";
    private static final String URI = "URI";

    // TODO: Rename and change types of parameters
    private String docID;
    private String userID;
    private String uri;

    public CommentFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static CommentFragment newInstance(String docID, String userID, String uri) {
        CommentFragment fragment = new CommentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DocID, docID);
        args.putString(ARG_USERID, userID);
        args.putString(URI, uri);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            docID = getArguments().getString(ARG_DocID);
            userID = getArguments().getString(ARG_USERID);
            uri = getArguments().getString(URI);
        }
    }

    FragmentCommentBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       binding = FragmentCommentBinding.inflate(inflater,container,false);
       return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Comments");

        commentsRecyclerView = binding.CommentsRV;
        commentsRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        commentsRecyclerView.setLayoutManager(layoutManager);
        adapter = new CommentsRecyclerAdapter(comments, userID, this);
        imageOwner = binding.tvImageOwner;
        postBtn = binding.buttonPost;
        nComment = binding.etComment;
        imageDisp = binding.ivSelectedImage;
        Glide.with(this)
                .load(uri)
                .into(imageDisp);



        commentsRecyclerView.setAdapter(adapter);

        DocumentReference docRef = db.collection("images").document(docID);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("demo", "DocumentSnapshot data: " + document.getData());
                        imageOwner.setText(document.getString("photoOwner"));


                        getCommentData();



                    } else {
                        Log.d("demo", "No such document");
                    }
                } else {
                    Log.d("demo", "get failed with ", task.getException());
                }
            }
        });

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = nComment.getText().toString();
                //comments.add(input);
                setComment(input, docID, userID);


                Log.d("demo", "onClick: " + comments);
            }

        });

    }
    private void setComment(String input, String docID, String userID) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        HashMap<String, Object> comment = new HashMap<>();

        Log.d("demo", "setComment: creator of comment " + mAuth.getCurrentUser().getDisplayName());

        Log.d("demo", "setData: title is " + input);
        Log.d("demo", "setData: desc is " + docID);

        comment.put("creatorComment", mAuth.getCurrentUser().getDisplayName());
        comment.put("comment", input);
        comment.put("photoID", docID);
        comment.put("userID", userID);

        db.collection("images").document(docID).collection("comment").document().set(comment)

                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("demo", "onSuccess: Forum Successfully posted ");
                        getCommentData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("demo", "onFailure: Fail" + e.toString());
                    }
                });


    }
    void getCommentData() {
        comments.clear();
        db.collection("images").document(docID).collection("comment").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {



                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                            commentId = doc.getId();

                            comments.add(new Comment(doc.getString("creatorComment"), doc.getString("comment"), doc.getString("photoID"), doc.getString("userID"), commentId));
                        }
                        for (Comment comment : comments) {
                            Log.d("demo", "onSuccess: comment info " + comment);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onDeleteClick(int position) {
        deleteComment(position);

    }
    void deleteComment(int p) {

        String commentID = comments.get(p).commentId;

        Log.d("demo", "deleteComment: forum at comment " + comments.get(p));

        Map<String, Object> commentN = new HashMap<>();
        commentN.put("creatorComment", FieldValue.delete());
        commentN.put("comment", FieldValue.delete());
        commentN.put("photoID", FieldValue.delete());
        commentN.put("userID", FieldValue.delete());

        db.collection("images").document(docID).collection("comment").document(commentID).update(commentN).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("demo", "onSuccess: Success in deleting fields");
            }

        });

        //db.collection("forums").document(docID).collection("comment")
        db.collection("forums").document(docID).collection("comment").document(commentID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("demo", "onSuccess: Deleted successfully");
                        Log.d("demo", "onSuccess: ");
                        getCommentData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("demo", "onFailure: Error Deleting Document " + e);
                    }
                });
        adapter.notifyDataSetChanged();

    }
}



class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.CommentViewHolder>{
    ArrayList<Comment> comments;
    String userID;
    private onDeleteClick monDeleteListener;


    public CommentsRecyclerAdapter(ArrayList<Comment> comments, String userID, onDeleteClick onDeleteListener) {
        this.comments = comments;
        this.userID = userID;
        this.monDeleteListener = onDeleteListener;

    }

    @NonNull
    @Override
    public CommentsRecyclerAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_items,parent,false);
        CommentViewHolder commentViewHolder = new CommentViewHolder(view, monDeleteListener);
        return commentViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsRecyclerAdapter.CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.textViewCommentCreator.setText(comment.commentCreator);
        holder.textViewComment.setText(comment.comment);
        if(userID.equals(comment.userID)){
            holder.imageViewTrash.setImageResource(R.drawable.rubbish_bin);
            holder.imageViewTrash.setVisibility(View.VISIBLE);
        }else {
            holder.imageViewTrash.setVisibility(View.INVISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return this.comments.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textViewCommentCreator;
        TextView textViewComment;
        ImageView imageViewTrash;
        onDeleteClick onDeleteListener;

        Comment comment;
        public CommentViewHolder(@NonNull View itemView, onDeleteClick onDeleteListener) {
            super(itemView);
            textViewComment = itemView.findViewById(R.id.tvComment);
            textViewCommentCreator = itemView.findViewById(R.id.tvCommentCreator);
            imageViewTrash = itemView.findViewById(R.id.ivTrash);

            itemView.setOnClickListener(this);
            this.onDeleteListener = onDeleteListener;

            if(imageViewTrash!=null){
                imageViewTrash.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
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
}