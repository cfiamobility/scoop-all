package ca.gc.inspection.scoop.editprofile;

import ca.gc.inspection.scoop.*;
import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ca.gc.inspection.scoop.ImageFilePath;
import ca.gc.inspection.scoop.searchbuilding.SearchBuildingActivity;
import ca.gc.inspection.scoop.util.CameraUtils;
import ca.gc.inspection.scoop.util.NetworkUtils;
import de.hdodenhof.circleimageview.CircleImageView;

import static ca.gc.inspection.scoop.util.CameraUtils.imageOrientationValidator;
import static ca.gc.inspection.scoop.util.StringUtils.capitalizeFirstLetter;
import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

public class EditProfileActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener, EditProfileContract.View {
	/**
	 * Implements the View in the EditProfileContract interface to follow MVP architecture.
	 * Allows the user to edit their profile information such as their first name, last name, address,
     * and social media.
	 */

    private enum EditTextType {
        POSITION, ADDRESS, DIVISION
    }

    private EditProfileContract.Presenter mPresenter;

	// Request codes for intents
	public static final int TAKE_PIC_REQUEST_CODE = 0;
	public static final int CHOOSE_PIC_REQUEST_CODE = 1;
	public static final int CHOOSE_BUILDING_REQUEST_CODE = 2;
	private static final int MY_CAMERA_PERMISSION_CODE = 100;

	// UI Declarations
	AutoCompleteTextView positionET, divisionET;
	EditText firstNameET, lastNameET, linkedinET, twitterET, facebookET, instagramET;
	TextView changeProfilePicBTN;
	CircleImageView previewProfilePic;
	CircleImageView profilePreview;
	Button saveBTN;

	TextView enterBuildingBTN;
	ImageButton deleteBuildingBTN;


	// Application Side Variable Declarations
	static String userID, currentPhotoPath;
	Uri mMediaUri;
	static Bitmap bitmap;

	// stored local variables
	String mBuildingId;

	// The method that runs when save is pressed
	private View.OnClickListener save = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// changes the bitmap into a string encoded using base64
			String image = CameraUtils.bitmapToString(bitmap);

			// putting all the edittext fields into a hashmap to be passed into nodejs
			if (!(firstNameET.getText().toString().isEmpty()) && !(lastNameET.getText().toString().isEmpty())) {
				Map<String, String> params = new HashMap<>();
				params.put("userid", userID);
				params.put("firstname", firstNameET.getText().toString());
				params.put("lastname", lastNameET.getText().toString());
				params.put("position", positionET.getText().toString());
				params.put("division", divisionET.getText().toString());
				params.put("building", enterBuildingBTN.getText().toString());

				// params cant be null so we set buildingid to -1 if its empty
				if (mBuildingId != null){
					params.put("buildingid", mBuildingId);
				}
				else{
					params.put("buildingid", String.valueOf(-1));
				}

				Log.d("buildingid", Integer.toString(getIntent().getIntExtra("buildingid", -1)));
				params.put("linkedin", linkedinET.getText().toString());
				params.put("twitter", twitterET.getText().toString());
				params.put("instagram", instagramET.getText().toString());
				params.put("facebook", facebookET.getText().toString());
				params.put("image", image);
				mPresenter.updateUserInfo(NetworkUtils.getInstance(getApplicationContext()), params);
			} else {
				Toast.makeText(EditProfileActivity.this, getResources().getString(R.string.invalidNameEntry), Toast.LENGTH_SHORT).show();
			}
		}
	};


	private View.OnClickListener chooseBuilding = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getApplicationContext(), SearchBuildingActivity.class);
			startActivityForResult(intent, CHOOSE_BUILDING_REQUEST_CODE);
		}
	};

	private View.OnClickListener deleteBuilding = new View.OnClickListener(){
		@Override
		public void onClick(View v) {
			enterBuildingBTN.setText(null);
			mBuildingId = null;
		}
	};


	// method for the back button
	public void finishActivity(View view) {
		finish();
	}

	public void finishUpdateUserInfo() {
	    finish();
    }

    @Override
    public void setPresenter(@NonNull EditProfileContract.Presenter presenter) {
	    mPresenter = checkNotNull(presenter);
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profile);


		setPresenter(new EditProfilePresenter(this));

		// EditText Definitions
		firstNameET = findViewById(R.id.activity_edit_profile_et_first_name);
		lastNameET = findViewById(R.id.activity_edit_profile_et_last_name);
		positionET = findViewById(R.id.activity_edit_profile_et_position);
		divisionET = findViewById(R.id.activity_edit_profile_et_division);
		linkedinET = findViewById(R.id.activity_edit_profile_et_linkedin);
		twitterET = findViewById(R.id.activity_edit_profile_et_twitter);
		facebookET = findViewById(R.id.activity_edit_profile_et_facebook);
		instagramET = findViewById(R.id.activity_edit_profile_et_instagram);
		profilePreview = findViewById(R.id.activity_create_post_img_profile);

		// Building buttons
		enterBuildingBTN = findViewById(R.id.activity_edit_profile_txt_btn_choose_building);
		enterBuildingBTN.setOnClickListener(chooseBuilding);
		deleteBuildingBTN = findViewById(R.id.delete_building_button);
		deleteBuildingBTN.setOnClickListener(deleteBuilding);

		// Save Button Definition
		saveBTN = findViewById(R.id.activity_edit_profile_btn_save);
		saveBTN.setOnClickListener(save);

		// TextView Definition
		changeProfilePicBTN = findViewById(R.id.activity_edit_profile_txt_change_profile_picture);

		// ImageView Definition
		previewProfilePic = findViewById(R.id.activity_edit_profile_img_profile);

		// set the system status bar color
		getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.primary_dark));

		//Getting the userID
		SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("ca.gc.inspection.scoop", Context.MODE_PRIVATE);

		// DEFAULT TO BE CHANGED TO ""
		userID = sharedPreferences.getString("userid", "");

		Log.i("userid", userID + "_");

		// If userID is unavailable.
		if (userID.equals("")) {
			finish();
		}

		// Searches for user's info to autofill the edittext
		mPresenter.initialFill(NetworkUtils.getInstance(this));

		// Sets up the Position, Office Address and Division AutoCompletes
		autoComplete();

		// when the soft keyboard is open tapping anywhere else will close the keyboard
		findViewById(R.id.activity_edit_profile_layout).setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
				return true;
			}
		});
	}

	/**
	 * gets building data from the intent received after a user returns from selecting a building in the search building activity
	 * @param intent intent received after a user returns from selecting a building in the search building activity
	 */
	private void getBuilding(Intent intent) {
		//Intent intent = getIntent();
		String building = intent.getStringExtra("building");
		int buildingid = intent.getIntExtra("buildingid", -1);
		if (building == null){ // if null, then didnt return from search building activity or didnt select anything in search building activity
			return;
		}
		enterBuildingBTN.setText(building);
		mBuildingId = String.valueOf(buildingid);
		Log.d("changedBuildingid", mBuildingId);
		enterBuildingBTN.setTextColor(Color.parseColor("black"));
		return;
	}

	// Mandatory Methods for AdapterView.OnItemSelected - OnNothingSelected is Default Spinner Selection set to AB
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// Default selection
		//provinceSpinner.setSelection(0);
	}

	/**
     * Method to fill the existing boxes with information in the database
     * Runs when edit profile is clicked
	 * @param response
	 */
	public void setInitialFill(JSONObject response) {
		try {
			// If the user id was correct/matching
			if (response.get("userid").toString().equals(userID)) {
				// Name gets and sets
				String firstNameText = response.get("firstname").toString();
                String lastNameText = response.get("lastname").toString();
				firstNameET.setText(firstNameText);
				lastNameET.setText(lastNameText);

				// Profile picture setting and gtting
				bitmap = CameraUtils.stringToBitmap(response.get("profileimage").toString());
				previewProfilePic.setImageBitmap(bitmap);

				// If user has already inputted a position
				if (!response.get("positionid").toString().equals("null")) {
                    String positionText = response.get("positionname").toString();
					positionET.setText(positionText);
				}
				// If user has already inputted a division
				if (!response.get("divisionid").toString().equals("null")) {
                    String divisionText = response.get("division_en").toString();
					divisionET.setText(divisionText);
				}// If user has already inputted an office address
				if (!response.get("buildingid").toString().equals("null")) {
                    String buildingText = response.get("address").toString();
					enterBuildingBTN.setText(buildingText);

					mBuildingId = response.get("buildingid").toString();
					Log.d("setbuildingid", mBuildingId);

					enterBuildingBTN.setTextColor(Color.parseColor("black"));
					//getBuilding(); // gets building address in the case that the previous activity was "searchBuildingActivity" and the user selected a new building there
				}
				// If the user has already inputted a facebook url
				if (!response.get("facebook").toString().equals("null")) {
                    String facebookText = response.get("facebook").toString();
					facebookET.setText(facebookText);
				}
				// if the user has already inputted a twitter url
				if (!response.get("twitter").toString().equals("null")) {
                    String twitterText = response.get("twitter").toString();
					twitterET.setText(twitterText);
				}
				// If the user has already inputted a linkedin url
				if (!response.get("linkedin").toString().equals("null")) {
                    String linkedinText = response.get("linkedin").toString();
					linkedinET.setText(linkedinText);
				}
				// If the user has already inputted an instagram url
				if (!response.get("instagram").toString().equals("null")) {
                    String instagramText = response.get("instagram").toString();
					instagramET.setText(instagramText);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    /** Method to establish/set up the auto complete text views for position, division and building
     */
	private void autoComplete() {

		// Autocomplete for position edittext
		addTextChangedListenerTo(positionET, EditTextType.POSITION);

		// Autocomplete for the division edittext
		addTextChangedListenerTo(divisionET, EditTextType.DIVISION);
	}

	private void addTextChangedListenerTo(AutoCompleteTextView textView, EditTextType type) {
        textView.addTextChangedListener(new EditProfileTextWatcher(textView, type));
    }

    /** Method to setup the front end of autocomplete text view for positions
     *
     * @param positionAutoComplete
     */
    public void setPositionETAdapter(ArrayList<String> positionAutoComplete) {
        //array adapter to set the autocomplete menu
        ArrayAdapter positionAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, positionAutoComplete);
        positionET.setAdapter(positionAdapter);
    }

    /** Method to setup the front end of autocomplete text view for division
     *
     * @param divisionsAutoComplete
     */
    public void setDivisionETAdapter(ArrayList<String> divisionsAutoComplete) {
        // Setting up the adapter
        ArrayAdapter divisionAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, divisionsAutoComplete);
        divisionET.setAdapter(divisionAdapter);
    }

	// when the change profile picture text is tapped
	public void changeProfilePicture(View view) {

		// custom dialog box
		final Dialog dialog = new Dialog(EditProfileActivity.this);
		dialog.setContentView(R.layout.dialog_change_profile_picture);

		// Imageview inside the dialog box
		CircleImageView previewImage = dialog.findViewById(R.id.dialog_change_profile_picture_img_profile);
		previewImage.setImageBitmap(bitmap);

		// Button to take a picture
		Button takePhotoButton = dialog.findViewById(R.id.dialog_change_profile_picture_btn_photo);
		takePhotoButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// closing the dialog box
				dialog.dismiss();

				// Checks for permission to write (implicit read) externnal data and camera permissions
				if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
					requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_CAMERA_PERMISSION_CODE);
				} else {
					// Goes to this method to open camera
					takePicture();
				}
			}
		});

		// Button to open gallery and choose a picture
		Button photoGalleryButton = dialog.findViewById(R.id.dialog_change_profile_picture_btn_gallery);
		photoGalleryButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Closing the dialog box
				dialog.dismiss();

				// Intent to open gallery
				Intent choosePictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
				choosePictureIntent.setType("image/*");
				startActivityForResult(Intent.createChooser(choosePictureIntent, "Select Picture"), CHOOSE_PIC_REQUEST_CODE);
			}
		});

		// so the buttons are shown
		takePhotoButton.setEnabled(true);
		photoGalleryButton.setEnabled(true);

		// show the custom dialog
		dialog.show();
	}


	// Method thats runs when the user selects take a picture when changing profile picture
	public void takePicture() {
		CameraUtils.takePicture(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {

			// This runs if the user selected a building from the search building activity
			if (requestCode == CHOOSE_BUILDING_REQUEST_CODE){
				getBuilding(data);
				return;
			}

			// This runs if the user has successfully chosen a picture from their gallery
			if (requestCode == CHOOSE_PIC_REQUEST_CODE) {
				// If the image is 0kb, etc, error
				if (data == null) {
					Toast.makeText(getApplicationContext(), "Image cannot be null!", Toast.LENGTH_LONG).show();
				} else {
					try {
						// Get the data reference
						Uri uri = data.getData();

						// Gets the absolute path of the image to be stored into the db
						String realPath = ImageFilePath.getPath(EditProfileActivity.this, data.getData());

						// Gets the bitmap of the image to be editted to be rightside up
						Bitmap oldBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

						// sends the old bitmap through the orientation validator because android sucks and it takes pictures in landscape mode
						// android camera is a J O K E
						bitmap = imageOrientationValidator(oldBitmap, realPath);

						// Sets the imageview to the corrected bitmap
						previewProfilePic.setImageBitmap(bitmap);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				// This runs when the user has taken a picture

				// Intent to scan for the photo that was just taken
				Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

				// saves the file here
				File f = new File(currentPhotoPath);
				Uri contentUri = Uri.fromFile(f);
				mediaScanIntent.setData(contentUri);
				// Literally don't know what this does but itll break if you touch any of this. Copied most of this method from the documentation, goodluck.
				this.sendBroadcast(mediaScanIntent);

				// Scales the image - google : "android camera basics" and look for the documentation entitled: 'take a picture'. It's pretty bad but yea.
				int targetW = previewProfilePic.getWidth();
				int targetH = previewProfilePic.getHeight();
				BitmapFactory.Options bmOptions = new BitmapFactory.Options();
				bmOptions.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
				int photoW = bmOptions.outWidth;
				int photoH = bmOptions.outHeight;
				int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
				bmOptions.inJustDecodeBounds = false;
				bmOptions.inSampleSize = scaleFactor;
				bmOptions.inPurgeable = true;

				// getting the bitmap to be corrected
				Bitmap oldBitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

				// correcting the bitmap
				bitmap = imageOrientationValidator(oldBitmap, currentPhotoPath);

				// Setting the image
				previewProfilePic.setImageBitmap(bitmap);
			}

		} else if (resultCode != RESULT_CANCELED) {
			Toast.makeText(getApplicationContext(), "Cancelled!", Toast.LENGTH_LONG).show();
		}
	}

    /**
     * Receives the permission result. Starts an activity to take a picture if permission was granted
     * by the user.
     * Overrides Android Activity method.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == MY_CAMERA_PERMISSION_CODE) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
				Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
				takePicture();
			} else {
				Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
			}
		}
	}


    private class EditProfileTextWatcher implements TextWatcher {
	    private AutoCompleteTextView mTextView;
	    private EditTextType mType;

	    EditProfileTextWatcher(AutoCompleteTextView textView, EditTextType type) {
	        mTextView = textView;
            mType = type;
        }

        private void autoComplete(String textChangedCapitalized) {
	        switch (mType) {
                case POSITION:
                    mPresenter.getPositionAutoCompleteFromDB(NetworkUtils.getInstance(getApplicationContext()), textChangedCapitalized);
                    break;
                case DIVISION:
                    mPresenter.getDivisionAutoCompleteFromDB(NetworkUtils.getInstance(getApplicationContext()), textChangedCapitalized);
                    break;
	        }
        }

        // Mandatory Method
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        // Runs whenever the text is changed
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Only submits request if the entered text is >= 3 characters for specificity
            if (mTextView.getText().length() >= 2) {
                // Text inputted converted to have a cap letter to start ie. "start" -> "Start"
                String textChanged = mTextView.getText().toString();
                String textChangedCapitalized = capitalizeFirstLetter(textChanged);
                autoComplete(textChangedCapitalized);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            // necessary to implement interface
        }
    }

}
