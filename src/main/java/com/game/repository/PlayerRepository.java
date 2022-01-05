package com.game.repository;

import com.game.entity.Player;
import com.game.entity.PlayerForm;

import java.util.List;
import java.util.Map;

public interface PlayerRepository {
    Integer getPlayersCount(Map<String,String> params);
    List<Player> getListPlayer(Map<String,String> params);
    Player getById(Long id);
    boolean update(Player playerForUpdate, PlayerForm playerSource);
    Player create (Player playerSource);
    boolean delete (Player player);
}
