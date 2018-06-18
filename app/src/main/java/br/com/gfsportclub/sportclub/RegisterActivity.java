package br.com.gfsportclub.sportclub;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private GoogleSignInClient mGoogleSignInClient;
    private Button registerButton;
    private TextView cancelButton;
    private EditText edtEmail, edtSenha;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "SignInActivity";
    private static final String TAGF = "FacebookLogin";
    private static final int RC_SIGN_IN = 9002;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference mDatabase;
    private CallbackManager mCallbackManager;
    private boolean userCheck = false;

    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(RegisterActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        edtEmail = (EditText) findViewById(R.id.edtEmailRegister);
        edtSenha = (EditText) findViewById(R.id.edtSenhaRegister);
        cancelButton = (TextView) findViewById(R.id.cancel_button_r);
        registerButton = (Button) findViewById(R.id.register_button);




        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    final String userEmail, userSenha;

                    userEmail = edtEmail.getText().toString().trim();
                    userSenha = edtSenha.getText().toString().trim();

                    if(TextUtils.isEmpty(userEmail)){
                        Toast.makeText(getApplicationContext(),"Entre com um email!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(TextUtils.isEmpty(userSenha)){
                        Toast.makeText(getApplicationContext(),"Entre com uma senha!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(userSenha.length() < 6){
                        Toast.makeText(getApplicationContext(),"A senha é muito curta!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(!TextUtils.isEmpty(userEmail) && !TextUtils.isEmpty(userSenha)){

                        mAuth.createUserWithEmailAndPassword(userEmail,userSenha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(RegisterActivity.this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show();
                                    mDatabase.child(mAuth.getUid()).child("email").setValue(userEmail);
                                    mAuth.signInWithEmailAndPassword(userEmail, userSenha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()){
                                                    Intent intent = new Intent(RegisterActivity.this, RegisterFormActivity.class);
                                                    startActivity(intent);
                                                } else {
                                                    Toast.makeText(RegisterActivity.this, "Falha ao resgistrar!", Toast.LENGTH_LONG).show();
                                                }
                                        }
                                    });

                                } else {
                                    Toast.makeText(RegisterActivity.this, "Falha ao criar conta", Toast.LENGTH_SHORT);
                                }
                            }
                        });
                    } else {
                        Toast.makeText(RegisterActivity.this, "Preencha os campos acima.", Toast.LENGTH_SHORT);
                    }
            }
        });

        findViewById(R.id.google_register).setOnClickListener(this);

        // [START initialize_fblogin]
        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.facebook_register);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAGF, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAGF, "facebook:onCancel");
                // [START_EXCLUDE]
                //updateUI(null);
                // [END_EXCLUDE]
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAGF, "facebook:onError", error);
                // [START_EXCLUDE]
                //updateUI(null);
                // [END_EXCLUDE]
            }
        });
        // [END initialize_fblogin]



    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_register:
                signIn();
                break;
            // ...
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            user = mAuth.getCurrentUser();
                            checkIfExist();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }


    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAGF, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAGF, "signInWithCredential:success");
                            user = mAuth.getCurrentUser();
                            checkIfExist();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAGF, "signInWithCredential:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                    }
                });
    }

    private void signOut() {
        mAuth.signOut();
        mGoogleSignInClient.signOut();
        LoginManager.getInstance().logOut();
    }

    private void checkIfExist(){

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(user.getUid())){
                    Toast.makeText(RegisterActivity.this, "Usuário já cadastrado", Toast.LENGTH_SHORT).show();
                    signOut();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                } else {
                    mDatabase.child(user.getUid()).child("email").setValue(user.getEmail());
                    startActivity(new Intent(RegisterActivity.this, RegisterFormActivity.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


}
