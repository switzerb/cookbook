package com.brennaswitzer.cookbook.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("WeakerAccess")
@Embeddable
public class Acl {

    @NotNull
    @ManyToOne
    private User owner;

    @ElementCollection
    @MapKeyJoinColumn(name = "user_id")
    @Column(name = "perm")
    @Enumerated(EnumType.STRING)
    private Map<User, Permission> grants;

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
        // clear any explicit grant the new owner previously had
        if (grants == null) return;
        grants.remove(owner);
    }

    public Set<User> getGrantedUsers() {
        if (grants == null) {
            //noinspection unchecked
            return Collections.EMPTY_SET;
        }
        return grants.keySet();
    }

    public Permission getGrant(User user) {
        if (user == null) throw new IllegalArgumentException("The null user can't have an access grant.");
        if (user.equals(owner)) return Permission.ADMINISTER;
        if (grants == null) return null;
        return grants.get(user);
    }

    public Permission setGrant(User user, Permission perm) {
        if (user == null) throw new IllegalArgumentException("You can't grant access to the null user.");
        if (user.equals(owner)) throw new UnsupportedOperationException();
        if (grants == null) grants = new HashMap<>();
        return grants.put(user, perm);
    }

    public Permission deleteGrant(User user) {
        if (user == null) throw new IllegalArgumentException("You can't revoke access from the null user.");
        if (user.equals(owner)) throw new UnsupportedOperationException();
        if (grants == null) return null;
        return grants.remove(user);
    }

}