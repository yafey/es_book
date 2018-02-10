package com.lida.es_book.repository;

import com.lida.es_book.entity.User;
import org.springframework.data.repository.CrudRepository;

/*
 *Created by LidaDu on 2018/2/6.  
 */
public interface UserDao extends CrudRepository<User, String> {

    User findByTelephone(String telephone);

}
