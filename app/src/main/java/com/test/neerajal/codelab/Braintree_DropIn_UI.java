
package com.test.neerajal.codelab;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.AndroidPay;
import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.exceptions.BraintreeError;
import com.braintreepayments.api.exceptions.ErrorWithResponse;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.interfaces.BraintreeResponseListener;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wallet.Cart;
import com.google.android.gms.wallet.FullWallet;
import com.google.android.gms.wallet.LineItem;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.WalletConstants;
import com.google.android.gms.wallet.fragment.SupportWalletFragment;

public class Braintree_DropIn_UI extends AppCompatActivity {



    private static final String TOKENIZATION_KEY = "sandbox_ngby7wk5_z262mwmmnhc35t25";
    private int REQUEST_CODE = 1000;
    public static final int MASKED_WALLET_REQUEST_CODE = 888;
    private static final int REQUEST_CODE_CHANGE_MASKED_WALLET = 1002;
    private BraintreeFragment mBraintreeFragment;
    private static final String mAuthorization = "z262mwmmnhc35t25";


    private GoogleApiClient mGoogleApiClient;
    private String MERCHANT_NAME = "My Codelab";

    private static final String TAG = MainActivity.class.getSimpleName() ;
    private SupportWalletFragment mWalletFragment;

    private MaskedWallet mMaskedWallet;
    public static final int FULL_WALLET_REQUEST_CODE = 889;
    private FullWallet mFullWallet;
    private Button confirmButton;
    private Button add_ccButton;
    String googleTransactionId;
    String merchantTranasctionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_braintree__drop_in__ui);


        try {
            mBraintreeFragment = BraintreeFragment.newInstance(this, TOKENIZATION_KEY);


        } catch (InvalidArgumentException e) {
            // Something was wrong with your authorization string
        }
    }



    public void onPaymentButton_dropin(View v) throws InvalidArgumentException {
        Cart cart = Cart.newBuilder()
                .setCurrencyCode("USD")
                .setTotalPrice("10.00")
                .addLineItem(LineItem.newBuilder()
                        .setCurrencyCode("USD")
                        .setDescription("Description")
                        .setQuantity("1")
                        .setUnitPrice("10.00")
                        .setTotalPrice("10.00")
                        .build())
                .build();

        DropInRequest dropInRequest = new DropInRequest()
                .tokenizationKey(TOKENIZATION_KEY)
                .androidPayCart(cart);

        // REQUEST_CODE is arbitrary and is only used within this activity
        startActivityForResult(dropInRequest.getIntent(this), REQUEST_CODE);




    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
            Toast.makeText(this," Braintree DropIn Result:" + result, Toast.LENGTH_SHORT).show();
            // send result.getPaymentMethodNonce().getNonce() to your server
        } else {
            // handle errors here, an error may be available in
            Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
        }


        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MASKED_WALLET_REQUEST_CODE:
                switch (resultCode) {
                    case RESULT_OK:
                        mMaskedWallet = data
                                .getParcelableExtra(WalletConstants.EXTRA_MASKED_WALLET);

                        googleTransactionId = mMaskedWallet.getGoogleTransactionId();
                        merchantTranasctionId = mMaskedWallet.getMerchantTransactionId();




                        Toast.makeText(this, "Braintree - Got Masked Wallet  " , Toast.LENGTH_SHORT).show();
                        break;
                    case RESULT_CANCELED:
                        // The user canceled the operation
                        break;
                    case WalletConstants.RESULT_ERROR:
                        Toast.makeText(this, "An Error Occurred", Toast.LENGTH_SHORT).show();
                        break;
                }
                break;

            case REQUEST_CODE_CHANGE_MASKED_WALLET:
                if (resultCode == RESULT_OK &&
                        data.hasExtra(WalletConstants.EXTRA_MASKED_WALLET)) {
                    mMaskedWallet = data.getParcelableExtra(WalletConstants.EXTRA_MASKED_WALLET);


                }
                // you may also want to use the new masked wallet data here, say to recalculate
                // shipping or taxes if shipping address changed
                break;

            case FULL_WALLET_REQUEST_CODE:
                switch (resultCode) {
                    case RESULT_OK:
                        mFullWallet = data
                                .getParcelableExtra(WalletConstants.EXTRA_FULL_WALLET);

                        AndroidPay.tokenize(mBraintreeFragment, mFullWallet);

                        // Show the credit card number
                        Toast.makeText(this,
                                "Got Full Wallet - Payment Nonce : ",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case WalletConstants.RESULT_ERROR:
                        Toast.makeText(this, "An Error Occurred", Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
        }
    }

}


    public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
        // Send this nonce to your server
        String nonce = paymentMethodNonce.getNonce();
    }


    public void onCancel(int requestCode) {
        // Use this to handle a canceled activity, if the given requestCode is important.
        // You may want to use this callback to hide loading indicators, and prepare your UI for input
    }

    public void onError(Exception error) {
        if (error instanceof ErrorWithResponse) {
            ErrorWithResponse errorWithResponse = (ErrorWithResponse) error;
            //BraintreeError cardErrors = cardErrors.errorFor();
           // if (cardErrors != null) {


// There is an issue with the credit card.
                /*BraintreeError expirationMonthError = cardErrors.errorFor("expirationMonth");
                if (expirationMonthError != null) {
                    // There is an issue with the expiration month.
                    setErrorMessage(expirationMonthError.getMessage());
                }*/

            }
        }
    }




