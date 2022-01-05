package com.game.service;

import com.game.entity.Player;
import com.game.entity.PlayerForm;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class PlayerServiceImpl implements PlayerRepository {

    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public PlayerServiceImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Player create(Player player) {
        calcPlayerParameters(player);
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(player);
            entityManager.flush();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            return null;
        }
        entityManager.getTransaction().commit();
        entityManager.refresh(player);
        entityManager.close();
        return player;
    }

    @Override
    public Integer getPlayersCount(Map<String,String> params) {
        addDefaultParams(params);
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        return entityManager.createQuery(getQueryCriteriaParamsForCount(entityManager,params))
                .getSingleResult().intValue();
    }

    @Override
    public List getListPlayer(Map<String,String> params) {
        addDefaultParams(params);
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        int pageNumber = Integer.parseInt(params.get("pageNumber"));
        int pageSize = Integer.parseInt(params.get("pageSize"));
        return entityManager.createQuery(getQueryCriteriaParams(entityManager,params))
                .setFirstResult(pageNumber * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    private CriteriaQuery<Long> getQueryCriteriaParamsForCount(EntityManager entityManager, Map<String, String> params) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Player> playerRoot = criteriaQuery.from(Player.class);
        addCriteriaParams(criteriaBuilder, criteriaQuery, playerRoot, params);
        criteriaQuery.select(criteriaBuilder.count(playerRoot));
        return criteriaQuery;
    }

    private CriteriaQuery<Player> getQueryCriteriaParams(EntityManager entityManager, Map<String, String> params) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Player> criteriaQuery = criteriaBuilder.createQuery(Player.class);
        Root<Player> playerRoot = criteriaQuery.from(Player.class);
        addCriteriaParams(criteriaBuilder, criteriaQuery, playerRoot, params);
        String orderField = params.get("order").toLowerCase(Locale.ROOT);
        criteriaQuery.orderBy(criteriaBuilder.asc(playerRoot.get(orderField)));
        criteriaQuery.select(playerRoot);
        return criteriaQuery;
    }

    private void addCriteriaParams(CriteriaBuilder criteriaBuilder, CriteriaQuery<?> criteriaQuery, Root<Player> playerRoot, Map<String, String> params) {
        List<Predicate> predicateList = new ArrayList<>(params.size() - 3);
        for (Map.Entry<String,String> entry : params.entrySet()) {
            String fieldName = entry.getKey();
            String fieldValue = entry.getValue();
            if (fieldName.equals("banned")) {
                Boolean banned = Boolean.parseBoolean(fieldValue);
                predicateList.add(criteriaBuilder.equal(playerRoot.get(fieldName), banned));
            } else if (fieldName.equals("name") || fieldName.equals("title")) {
                predicateList.add(criteriaBuilder.like(playerRoot.get(fieldName), "%" + fieldValue + "%"));
            } else if (fieldName.equals("race")) {
                Race race = Race.valueOf(fieldValue.toUpperCase(Locale.ROOT));
                predicateList.add(criteriaBuilder.equal(playerRoot.get(fieldName), race));
            } else if (fieldName.equals("profession")) {
                Profession profession = Profession.valueOf(fieldValue.toUpperCase(Locale.ROOT));
                predicateList.add(criteriaBuilder.equal(playerRoot.get(fieldName), profession));
            } else if (fieldName.equals("after")) {
                // birthday filter
                Long afterDate = Long.parseLong(fieldValue);
                predicateList.add(criteriaBuilder.greaterThanOrEqualTo(playerRoot.get("birthday").as(Date.class), new Date(afterDate)));
            } else if (fieldName.equals("before")) {
                Long beforeDate = Long.parseLong(fieldValue);
                predicateList.add(criteriaBuilder.lessThanOrEqualTo(playerRoot.get("birthday").as(Date.class), new Date(beforeDate)));
            } else if (fieldName.equals("minExperience")) {
                // experience filter
                Integer minValue = Integer.parseInt(fieldValue);
                if (params.containsKey("maxExperience")) {
                    Integer maxValue = Integer.parseInt(params.get("maxExperience"));
                    predicateList.add(criteriaBuilder.between(playerRoot.get("experience"), minValue, maxValue));
                } else {
                    predicateList.add(criteriaBuilder.greaterThanOrEqualTo(playerRoot.get("experience"), minValue));
                }
            } else if (fieldName.equals("maxExperience")) {
                // if don't set param minExperience
                if (!params.containsKey("minExperience")) {
                    Integer maxValue = Integer.parseInt(fieldValue);
                    predicateList.add(criteriaBuilder.lessThanOrEqualTo(playerRoot.get("experience"), maxValue));
                }
            } else if (fieldName.equals("minLevel")) {
                // level filter
                Integer minValue = Integer.parseInt(fieldValue);
                if (params.containsKey("maxLevel")) {
                    Integer maxValue = Integer.parseInt(params.get("maxLevel"));
                    predicateList.add(criteriaBuilder.between(playerRoot.get("level"), minValue, maxValue));
                } else {
                    predicateList.add(criteriaBuilder.greaterThanOrEqualTo(playerRoot.get("level"), minValue));
                }
            } else if (fieldName.equals("maxLevel")) {
                // if don't set param minLevel
                if (!params.containsKey("minLevel")) {
                    Integer maxValue = Integer.parseInt(fieldValue);
                    predicateList.add(criteriaBuilder.lessThanOrEqualTo(playerRoot.get("level"), maxValue));
                }
            }
        }
        criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
    }

    @Override
    public Player getById(Long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Player> criteriaQuery = criteriaBuilder.createQuery(Player.class);
        Root<Player> playerRoot = criteriaQuery.from(Player.class);
        Predicate predicateId = criteriaBuilder.equal(playerRoot.get("id"), id);
        criteriaQuery.where(predicateId);
        Player player = null;
        try {
            player = entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (Exception e) {
        }
        entityManager.close();
        return player;
    }

    @Override
    public boolean update(Player playerForUpdate, PlayerForm playerSource) {
        boolean isChange = false;

        if (playerSource.getName() != null) {
            isChange = true;
            playerForUpdate.setName(playerSource.getName());
        }

        if (playerSource.getTitle() != null) {
            isChange = true;
            playerForUpdate.setTitle(playerSource.getTitle());
        }

        if (playerSource.getRace() != null) {
            isChange = true;
            playerForUpdate.setRace(playerSource.getRace());
        }

        if (playerSource.getProfession() != null) {
            isChange = true;
            playerForUpdate.setProfession(playerSource.getProfession());
        }

        if (playerSource.getBirthday() != null) {
            isChange = true;
            playerForUpdate.setBirthday(playerSource.getBirthday());
        }

        if (playerSource.getBanned() != null) {
            isChange = true;
            playerForUpdate.setBanned(playerSource.getBanned());
        }

        if (playerSource.getExperience() != null) {
            isChange = true;
            playerForUpdate.setExperience(playerSource.getExperience());
        }
        if (isChange) {
            calcPlayerParameters(playerForUpdate);
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            entityManager.merge(playerForUpdate);
            entityManager.getTransaction().commit();
            entityManager.close();
        }
        return true;
    }

    @Override
    public boolean delete(Player player) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        try {
            entityManager.remove(entityManager.contains(player) ? player : entityManager.merge(player));
            entityManager.flush();
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        entityManager.getTransaction().commit();
        entityManager.close();
        return true;
    }

    private void calcPlayerParameters(Player player) {
        int experience = player.getExperience();
        int level = (int) Math.floor((Math.sqrt(2500 + 200 * experience) - 50) / 100);
        player.setLevel(level);
        player.setUntilNextLevel(50 * (level + 1) * (level + 2) - experience);
    }

    private void addDefaultParams(Map<String,String> params) {
        if (!params.containsKey("order")) {
            params.put("order", "ID");
        }
        if (!params.containsKey("pageNumber")) {
            params.put("pageNumber", "0");
        }
        if (!params.containsKey("pageSize")) {
            params.put("pageSize", "3");
        }
    }


}
