Primary Functionalities
    User Authentication:
        Firebase Authentication is used for user login, registration, and profile management.
        Users can create accounts, log in, and log out.
        
    Photo Management:
        Users can upload photos to Firebase Storage.
        Metadata about photos (e.g., owner, URI) is stored in Firestore.
        
    Comment System:
        Users can add comments to photos, which are stored in Firestore as sub-collections under photo documents.
        Comments are associated with the user who posted them and the photo they belong to.
        
    Profile Management:
        Users can view their profile details, including uploaded photos.
        Profiles display photos associated with the user.
        
    Recycler Views:
        RecyclerView is used for displaying lists of photos and comments in a scrollable format.
        
    Fragments for UI:
        Different UI fragments handle login, registration, profile viewing, and listing photos.

    
Data Structures Used
 1. Firebase-Provided Structures:
      Firestore Documents: Photos and comments are stored as documents with key-value pairs.
      Firebase Authentication: Manages user accounts and authentication details.
      Firebase Storage: Used for storing photo files.
    
2.  Custom Classes:
      Photo: Represents a photo with attributes like:
        docId: Unique ID in Firestore.
        photoOwner: User who uploaded the photo.
        uri: URI of the photo in Firebase Storage.
        uid: User ID of the owner.
    
    Comment: Represents a comment with attributes like:
        commentCreator: Name of the commenter.
        comment: Content of the comment.
        photoId: ID of the photo the comment belongs to.
        userID: User ID of the commenter.
        commentId: Unique ID of the comment in Firestore.
    
3.  Firebase Collections and Subcollections:
      Images Collection: Stores photo metadata like owner, URI, and ID.
      Comment Subcollection: Nested under individual photo documents to store comments related to the photo.
      
4.  Android-Specific Structures:

        ArrayList:
          Used for managing lists of Photo and Comment objects.
        RecyclerView.Adapter:
          Used to bind Photo and Comment data to RecyclerView items.
        HashMap:
          Used for constructing Firestore documents to upload data.
          
5.  External Libraries:

      Glide: Used for loading and displaying images from Firebase Storage URIs.
      Gson (potential): May be used for JSON serialization/deserialization (not explicit here but typical in Firebase apps).

    
 Application Flow
  
    Login/Register:
      Users log in or register using Firebase Authentication.
    
    Photo Upload:
      Photos are selected from the device and uploaded to Firebase Storage.
      Metadata (e.g., URI, owner) is added to Firestore.
    
    Photo Listing:
      Photos are fetched from Firestore and displayed in a RecyclerView.
    
    Commenting:
      Users can add, view, and delete comments on photos.
