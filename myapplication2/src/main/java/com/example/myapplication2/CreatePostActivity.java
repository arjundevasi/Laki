
package com.example.myapplication2;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CreatePostActivity extends AppCompatActivity {

    RelativeLayout addImageLayout;
    RelativeLayout image1Layout;
    RelativeLayout image2Layout;
    RelativeLayout image3Layout;

    ImageView imageView1CancelButton;
    ImageView imageView2CancelButton;
    ImageView imageView3CancelButton;

    ActivityResultLauncher<Intent> galleryLauncher;

    ImageView imageView1;
    ImageView imageView2;
    ImageView imageView3;

    List<Uri> imageURIList = new ArrayList<>();

    String uid;
    String username;

    String profileUrl;

    ImageView profileImageview;
    TextView usernameTextview;

    Button postButton;

    EditText postText;

    int compressionQuality = 70;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        activityResultLaunchers();


        Toolbar toolbar = findViewById(R.id.create_post_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        imageView1 = findViewById(R.id.imageview1);
        imageView2 = findViewById(R.id.imageview2);
        imageView3 = findViewById(R.id.imageview3);

        profileImageview = findViewById(R.id.profile_imageview_create_post_activity);
        usernameTextview = findViewById(R.id.username_textview_create_post_activity);

        addImageLayout = findViewById(R.id.add_image_layout_create_post_activity);
        image1Layout = findViewById(R.id.imageview1_layout_create_post_activity);
        image2Layout = findViewById(R.id.imageview2_layout_create_post_activity);
        image3Layout = findViewById(R.id.imageview3_layout_create_post_activity);

        imageView1CancelButton = findViewById(R.id.cancel_imageview1);
        imageView2CancelButton = findViewById(R.id.cancel_imageview2);
        imageView3CancelButton = findViewById(R.id.cancel_imageview3);

        postButton = findViewById(R.id.post_button_create_post_activity);

        postText = findViewById(R.id.edittext_create_post_activity);

        if (uid == null) {
            uid = FirebaseAuth.getInstance().getUid();
            findUsernameandProfileUrl(uid);
        }


        addImageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                galleryLauncher.launch(Intent.createChooser(i, "Select Images"));
            }
        });

        // cancelling image selection
        imageView1CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!imageURIList.isEmpty()) {
                    imageURIList.remove(0);
                    setUriImages(imageURIList);
                }
            }
        });

        imageView2CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!imageURIList.isEmpty()) {
                    imageURIList.remove(1);
                    setUriImages(imageURIList);
                }
            }
        });

        imageView3CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!imageURIList.isEmpty()) {
                    imageURIList.remove(2);
                    setUriImages(imageURIList);
                }
            }
        });


        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preparePostForUpload();
            }
        });

    }

    private List<Uri> getSelectedUri(Intent data) {
        //List<Uri> uri = new ArrayList<>();
        if (data != null && data.getClipData() != null) {
            int count = data.getClipData().getItemCount();
            for (int i = 0; i < count; i++) {
                imageURIList.add(data.getClipData().getItemAt(i).getUri());
            }
        } else if (data != null && data.getData() != null) {
            imageURIList.add(data.getData());
        }
        return imageURIList;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void activityResultLaunchers() {
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        imageURIList = getSelectedUri(data);
                        setUriImages(imageURIList);
                    }
                });

    }

    private void setUriImages(List<Uri> uriList) {
        if (uriList.isEmpty()) {
            imageView1.setImageURI(null);
            imageView2.setImageURI(null);
            imageView3.setImageURI(null);
            image1Layout.setVisibility(View.GONE);
            image2Layout.setVisibility(View.GONE);
            image3Layout.setVisibility(View.GONE);
            return;
        }

        // remove earlier set pictures in imageview and make layout invisible
        imageView1.setImageURI(null);
        imageView2.setImageURI(null);
        imageView3.setImageURI(null);
        image1Layout.setVisibility(View.GONE);
        image2Layout.setVisibility(View.GONE);
        image3Layout.setVisibility(View.GONE);


        if (imageURIList.size() > 3) {
            Toast.makeText(this, "You can only select 3 images", Toast.LENGTH_SHORT).show();

            // trim the list to only show 3 images
            while (imageURIList.size() > 3) {
                imageURIList.remove(imageURIList.size() - 1);
            }
        }
        if (imageURIList.size() == 1) {
            image1Layout.setVisibility(View.VISIBLE);
            addImageLayout.setVisibility(View.VISIBLE);

            imageView1.setImageURI(imageURIList.get(0));
        } else if (imageURIList.size() == 2) {
            image1Layout.setVisibility(View.VISIBLE);
            image2Layout.setVisibility(View.VISIBLE);

            addImageLayout.setVisibility(View.VISIBLE);

            imageView1.setImageURI(imageURIList.get(0));
            imageView2.setImageURI(imageURIList.get(1));
        } else if (imageURIList.size() == 3) {
            image1Layout.setVisibility(View.VISIBLE);
            image2Layout.setVisibility(View.VISIBLE);
            image3Layout.setVisibility(View.VISIBLE);

            addImageLayout.setVisibility(View.GONE);

            imageView1.setImageURI(imageURIList.get(0));
            imageView2.setImageURI(imageURIList.get(1));
            imageView3.setImageURI(imageURIList.get(2));
        }
    }

    private void findUsernameandProfileUrl(String uid) {
        SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
        String sharedUsername = sharedPreferences.getString("username", null);
        String sharedprofileUrl = sharedPreferences.getString("profileUrl", null);

        if (sharedUsername == null || sharedprofileUrl == null) {
            DatabaseReference database = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("searchIndex").child(uid);
            database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        UploadPersonforSeachIndex uploadPersonforSeachIndex = snapshot.getValue(UploadPersonforSeachIndex.class);
                        if (uploadPersonforSeachIndex != null) {
                            username = uploadPersonforSeachIndex.getUsername();
                            profileUrl = uploadPersonforSeachIndex.getProfileUrl();
                            Picasso.get().load(profileUrl).transform(new RoundImageTransformation()).into(profileImageview);
                            usernameTextview.setText(username);
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    finish();
                    Log.d("CreatePostActivity", "unable to find username and photo");
                }
            });
        } else {
            Picasso.get().load(sharedprofileUrl).transform(new RoundImageTransformation()).into(profileImageview);
            usernameTextview.setText(sharedUsername);
        }
    }

    private void preparePostForUpload() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String text = postText.getText().toString().trim();
        String postId = uid + "_" + timestamp;

        Bitmap bitmap1 = null;
        Bitmap bitmap2 = null;
        Bitmap bitmap3 = null;

        if (imageView1.getDrawable() != null) {
            bitmap1 = ((BitmapDrawable) imageView1.getDrawable()).getBitmap();
        }

        if (imageView2.getDrawable() != null) {
            bitmap2 = ((BitmapDrawable) imageView2.getDrawable()).getBitmap();

        }

        if (imageView3.getDrawable() != null) {
            bitmap3 = ((BitmapDrawable) imageView3.getDrawable()).getBitmap();

        }

        if (bitmap1 != null && bitmap2 == null && bitmap3 == null) {
            uploadImages(uid, timestamp, text, postId, bitmap1, null, null);
        }

        if (bitmap1 != null && bitmap2 != null && bitmap3 == null) {
            uploadImages(uid, timestamp, text, postId, bitmap1, bitmap2, null);
        }

        if (bitmap1 != null && bitmap2 != null && bitmap3 != null) {
            uploadImages(uid, timestamp, text, postId, bitmap1, bitmap2, bitmap3);
        }

        if (bitmap1 == null && bitmap2 == null && bitmap3 == null && !text.isEmpty()) {
            uploadPost(uid, timestamp, text, postId, null, null, null);
        }

        if (bitmap1 == null && bitmap2 == null && bitmap3 == null && text.isEmpty()) {
            Toast.makeText(this, "Please add image or text", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImages(String uid, String timestamp, String text, String postId, Bitmap bitmap1, Bitmap bitmap2, Bitmap bitmap3) {

        FirebaseStorage storage = FirebaseStorage.getInstance();

        if (bitmap1 != null && bitmap2 == null && bitmap3 == null) {


            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap1.compress(Bitmap.CompressFormat.JPEG, compressionQuality, byteArrayOutputStream);
            byte[] imagedata1 = byteArrayOutputStream.toByteArray();

            StorageReference storageReference = storage.getReference().child("post_images").child(postId).child("image1");
            UploadTask uploadTask = storageReference.putBytes(imagedata1);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String image1path = uri.toString();
                            uploadPost(uid, timestamp, text, postId, image1path, null, null);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreatePostActivity.this, "Unable to upload post", Toast.LENGTH_SHORT).show();
                            Log.d("CreatePostActivity", "failed on uploading image 1");
                        }
                    });
                }
            });
        }

        if (bitmap1 != null && bitmap2 != null && bitmap3 == null) {

            ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream();
            bitmap1.compress(Bitmap.CompressFormat.JPEG, compressionQuality, byteArrayOutputStream1);
            byte[] imageData1 = byteArrayOutputStream1.toByteArray();

            StorageReference storageReference1 = storage.getReference().child("post_images").child(postId).child("image1");
            UploadTask uploadTask1 = storageReference1.putBytes(imageData1);


            ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
            bitmap2.compress(Bitmap.CompressFormat.JPEG, compressionQuality, byteArrayOutputStream2);
            byte[] imageData2 = byteArrayOutputStream2.toByteArray();

            StorageReference storageReference2 = storage.getReference().child("post_images").child(postId).child("image2");
            UploadTask uploadTask2 = storageReference2.putBytes(imageData2);

            uploadTask1.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String image1path = uri.toString();
                            uploadTask2.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    storageReference2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String image2path = null;
                                            image2path = uri.toString();
                                            uploadPost(uid, timestamp, text, postId, image1path, image2path, null);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(CreatePostActivity.this, "Unable to upload post", Toast.LENGTH_SHORT).show();
                                            Log.d("CreatePostActivity", "Failed to upload image 2");
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(CreatePostActivity.this, "Unable to upload post", Toast.LENGTH_SHORT).show();
                                    Log.d("CreatePostActivity", "Failed to upload image 2");
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreatePostActivity.this, "Unable to upload post", Toast.LENGTH_SHORT).show();
                            Log.d("CreatePostActivity", "Failed to upload image 1");
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CreatePostActivity.this, "Unable to upload post", Toast.LENGTH_SHORT).show();
                    Log.d("CreatePostActivity", "Failed to upload image 1");
                }
            });
        }

        if (bitmap1 != null && bitmap2 != null && bitmap3 != null) {

            ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream();
            bitmap1.compress(Bitmap.CompressFormat.JPEG, compressionQuality, byteArrayOutputStream1);
            byte[] imageData1 = byteArrayOutputStream1.toByteArray();

            StorageReference storageReference1 = storage.getReference().child("post_images").child(postId).child("image1");
            UploadTask uploadTask1 = storageReference1.putBytes(imageData1);


            ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
            bitmap2.compress(Bitmap.CompressFormat.JPEG, compressionQuality, byteArrayOutputStream2);
            byte[] imageData2 = byteArrayOutputStream2.toByteArray();

            StorageReference storageReference2 = storage.getReference().child("post_images").child(postId).child("image2");
            UploadTask uploadTask2 = storageReference2.putBytes(imageData2);


            ByteArrayOutputStream byteArrayOutputStream3 = new ByteArrayOutputStream();
            bitmap3.compress(Bitmap.CompressFormat.JPEG, compressionQuality, byteArrayOutputStream3);
            byte[] imageData3 = byteArrayOutputStream3.toByteArray();

            StorageReference storageReference3 = storage.getReference().child("post_images").child(postId).child("image3");
            UploadTask uploadTask3 = storageReference3.putBytes(imageData3);


            // start uploading images one by one

            uploadTask1.addOnSuccessListener(taskSnapshot -> {
                storageReference1.getDownloadUrl().addOnSuccessListener(uri -> {
                    String image1path = uri.toString();
                    uploadTask2.addOnSuccessListener(taskSnapshot1 -> {
                        storageReference2.getDownloadUrl().addOnSuccessListener(uri1 -> {
                            String image2path = uri1.toString();
                            uploadTask3.addOnSuccessListener(taskSnapshot2 -> {
                                storageReference3.getDownloadUrl().addOnSuccessListener(uri2 -> {
                                    String image3path = uri2.toString();
                                    uploadPost(uid, timestamp, text, postId, image1path, image2path, image3path);
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(CreatePostActivity.this, "Unable to upload post", Toast.LENGTH_SHORT).show();
                                    Log.d("CreatePostActivity", "Failed to upload image 3");
                                });
                            }).addOnFailureListener(e -> {
                                Toast.makeText(CreatePostActivity.this, "Unable to upload post", Toast.LENGTH_SHORT).show();
                                Log.d("CreatePostActivity", "Failed to upload image 3");
                            });
                        }).addOnFailureListener(e -> {
                            Toast.makeText(CreatePostActivity.this, "Unable to upload post", Toast.LENGTH_SHORT).show();
                            Log.d("CreatePostActivity", "Failed to upload image 2");
                        });
                    }).addOnFailureListener(e -> {
                        Toast.makeText(CreatePostActivity.this, "Unable to upload post", Toast.LENGTH_SHORT).show();
                        Log.d("CreatePostActivity", "Failed to upload image 2");
                    });
                }).addOnFailureListener(e -> {
                    Toast.makeText(CreatePostActivity.this, "Unable to upload post", Toast.LENGTH_SHORT).show();
                    Log.d("CreatePostActivity", "Failed to upload image 1");
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(CreatePostActivity.this, "Unable to upload post", Toast.LENGTH_SHORT).show();
                Log.d("CreatePostActivity", "Failed to upload image 1");
            });
        }
        //end
    }

    private void uploadPost(String uid, String timestamp, String text, String postId, String image1path, String image2path, String image3path) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference reference = firebaseDatabase.getReference().child("posts").child(postId);

        PostObject post = new PostObject(uid, timestamp, postId, text, image1path, image2path, image3path);

        reference.setValue(post, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error != null) {
                    Toast.makeText(CreatePostActivity.this, "Please try again later", Toast.LENGTH_SHORT).show();
                    Log.d("CreatePostActivity", "error in saving post to database");
                } else {
                    DatabaseReference postTrendsReference = firebaseDatabase.getReference().child("postTrend").child(postId);
                    PostTrendObject postTrendObject = new PostTrendObject(postId, 0, 0, 0, 0);
                    postTrendsReference.setValue(postTrendObject);

                    DatabaseReference postIndexReference = firebaseDatabase.getReference().child("postIndex").child(uid).child(postId);
                    PostIndexObject postIndexObject = new PostIndexObject(postId, uid, timestamp);
                    postIndexReference.setValue(postIndexObject);

                    finish();
                    //TODO SHOW A PROGRESS BAR HERE
                }
            }
        });

    }
}