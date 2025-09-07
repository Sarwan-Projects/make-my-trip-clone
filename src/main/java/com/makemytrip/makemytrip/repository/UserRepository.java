package com.makemytrip.makemytrip.repository;

import com.makemytrip.makemytrip.models.Users;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<Users, String>
{
    public Users findByEmail(String email);
}
