/*
 * Copyright (C) 2012 Sergej Dechand <cryptocall@serj.de>
 *                    Dominik Schürmann <dominik@dominikschuermann.de>
 * 
 * This file is part of CryptoCall.
 * 
 * CryptoCall is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CryptoCall is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CryptoCall.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.cryptocall.ui;

import java.util.List;

import org.cryptocall.CryptoCallApplication;
import org.cryptocall.R;

import org.cryptocall.util.Constants;
import org.cryptocall.util.Log;
import org.cryptocall.util.PreferencesHelper;
import org.cryptocall.util.QrCodeUtils;
import org.thialfihar.android.apg.service.IApgKeyService;
import org.thialfihar.android.apg.service.handler.IApgGetKeyringsHandler;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class BaseInformationFragment extends Fragment {
    private Activity mActivity;
    private CryptoCallApplication mApplication;
    private IApgKeyService mIApgKeyService;

    private TextView mKeyTextView;
    private TextView mTelTextView;
    private TextView mPgpMailTextView;
    private ImageView mQrCodeImageView;

    private Bitmap mQrCodeBitmap;

    /**
     * Inflate the layout for this fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.base_information_fragment, container, false);

        // get views
        mTelTextView = (TextView) view.findViewById(R.id.base_information_fragment_tel);
        mPgpMailTextView = (TextView) view.findViewById(R.id.base_information_fragment_pgp_mail);
        mKeyTextView = (TextView) view.findViewById(R.id.base_information_fragment_key);
        mQrCodeImageView = (ImageView) view.findViewById(R.id.base_information_fragment_qr);

        mQrCodeImageView.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
                QrCodeUtils.showQrCode(mActivity, mQrCodeBitmap);
            }
        });

        return view;
    }

    /**
     * onDestroy of view, recycle QRCodeBitmap
     */
    @Override
    public void onDestroyView() {
        if (mQrCodeBitmap != null) {
            mQrCodeBitmap.recycle();
            mQrCodeBitmap = null;
        }

        super.onDestroyView();
    }

    /**
     * onResume set informations and QrCode
     */
    @Override
    public void onResume() {
        super.onResume();

        // get public keyring for qr code
        try {
            mIApgKeyService.getPublicKeyRings(
                    new long[] { PreferencesHelper.getPgpMasterKeyId(mActivity) }, true,
                    getPublicKeyringHandler);
        } catch (RemoteException e) {
            Log.e(Constants.TAG, "RemoteException", e);
        }

        // set textview from preferenes
        mTelTextView.setText(PreferencesHelper.getTelephoneNumber(mActivity));
        mPgpMailTextView.setText(PreferencesHelper.getPgpEmail(mActivity));
        mKeyTextView.setText("" + PreferencesHelper.getPgpMasterKeyId(mActivity));

        Log.d(Constants.TAG,
                "PreferencesHelper.getPgpMasterKeyId(mActivity)"
                        + PreferencesHelper.getPgpMasterKeyId(mActivity));
    }

    private final IApgGetKeyringsHandler.Stub getPublicKeyringHandler = new IApgGetKeyringsHandler.Stub() {

        @Override
        public void onException(final int exceptionId, final String message) throws RemoteException {
            mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    Log.e(Constants.TAG, "exc" + message);
                }
            });
        }

        @Override
        public void onSuccess(final byte[] outputBytes, final List<String> outputStrings)
                throws RemoteException {
            mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    String keyring = outputStrings.get(0);

                    // populate qrcode representation
                    mQrCodeBitmap = QrCodeUtils.getQRCodeBitmap(keyring, 1000);
                    mQrCodeImageView.setImageBitmap(mQrCodeBitmap);
                }
            });

        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mApplication = (CryptoCallApplication) getActivity().getApplication();

        mIApgKeyService = mApplication.getApgKeyService();
    }
}
