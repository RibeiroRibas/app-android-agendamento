package br.com.beautystyle.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import br.com.beautystyle.model.UserLogin;
import br.com.beautystyle.model.UserToken;
import br.com.beautystyle.model.entity.User;
import br.com.beautystyle.repository.Resource;
import br.com.beautystyle.repository.UserRepository;

public class UserViewModel extends ViewModel {

    private final UserRepository repository;

    public UserViewModel(UserRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<User>> getByEmail(String email) {
        return repository.getByEmail(email);
    }

    public LiveData<Resource<UserToken>> authUser(UserLogin userLogin) {
        return repository.authUser(userLogin);
    }

}
