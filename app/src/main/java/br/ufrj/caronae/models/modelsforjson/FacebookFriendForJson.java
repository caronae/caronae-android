package br.ufrj.caronae.models.modelsforjson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import br.ufrj.caronae.models.User;

public class FacebookFriendForJson {

    @SerializedName("mutual_friends")
    List<User> mutualFriends;
    @SerializedName("total_count")
    int totalCount;

    public List<User> getMutualFriends() {
        return mutualFriends;
    }

    public void setMutualFriends(List<User> mutualFriends) {
        this.mutualFriends = mutualFriends;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
