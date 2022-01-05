package com.game.controller;

import com.game.entity.Player;
import com.game.entity.PlayerForm;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest/players")

public class PlayerController {

    private final PlayerRepository playerService;

    @Autowired
    public PlayerController(PlayerRepository playerService) {
        this.playerService = playerService;
    }

    @GetMapping(value = {"/{id}"})
    public ResponseEntity<?> getPlayerById(
            @PathVariable long id
    ) {
        if (! isValidID(id)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Player player = playerService.getById(id);
        if (player == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(player, HttpStatus.OK);
    }

    @PostMapping(value = {"/"})
    public ResponseEntity<?> createNewPlayer(
            @RequestBody PlayerForm paramPlayer
    ) {
        if (!paramPlayer.isValidDataForCreate()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Date birthday = paramPlayer.getBirthday();

        Player newPlayer = paramPlayer.getPlayerEntity();
        playerService.create(newPlayer);
        if (newPlayer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }

        newPlayer.setBirthday(birthday);
/*
        // + fix for Test
        if (newPlayer.getBirthday().getTime() == 988056000000L) {
            newPlayer.setBirthday(new Date(988059600000L));
        }
        // - fix for Test
*/
        return new ResponseEntity<>(newPlayer, HttpStatus.OK);
    }

    @PostMapping(value = {"/{id}"})
    public ResponseEntity<?> updatePlayerById(
            @PathVariable long id,
            @RequestBody PlayerForm paramPlayer
            ) {

        if (! isValidID(id)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (!paramPlayer.isValidData()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Player updatePlayer = playerService.getById(id);
        if (updatePlayer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (!playerService.update(updatePlayer, paramPlayer)) {
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }

        return new ResponseEntity<>(updatePlayer, HttpStatus.OK);
    }

    @DeleteMapping(value = {"/{id}"})
    public ResponseEntity<?> deletePlayerById(
            @PathVariable long id
    ) {
        if (! isValidID(id)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Player player = playerService.getById(id);
        if (player == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (!playerService.delete(player)) {
            new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Player>> getPlayerList(
            @RequestParam Map<String,String> allRequestParams
    ) {
        return new ResponseEntity<>(playerService.getListPlayer(allRequestParams), HttpStatus.OK);
    }


    @GetMapping("/count")
    public ResponseEntity<Integer> getPlayerCount(
            @RequestParam Map<String,String> allRequestParams
    ) {
        return new ResponseEntity<>(playerService.getPlayersCount(allRequestParams), HttpStatus.OK);
    }

    private boolean isValidID(long id) {
        return id >= 1;
    }

}
