package net.alecks.helpmewoof.ui.registrarse;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RegistrarseViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public RegistrarseViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is registrarse fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}