package io.github.murphy955.fina.domain.entity.athlete;

import io.github.murphy955.fina.domain.enm.Stroke;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 接力队实体
 * <p>
 * 描述接力项目的参赛队伍，包含队名、4 名队员及接力泳序。
 *
 * @author : 李泽聿
 * @since : 2026:05:12 17:05
 */
public class RelayTeam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String teamName;

    private List<Athlete> members = new ArrayList<>();

    private List<Stroke> strokeOrder = new ArrayList<>();

    public RelayTeam() {
    }

    public RelayTeam(String teamName) {
        this.teamName = Objects.requireNonNull(teamName, "teamName must not be null");
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public List<Athlete> getMembers() {
        return Collections.unmodifiableList(members);
    }

    public void setMembers(List<Athlete> members) {
        this.members = members == null ? new ArrayList<>() : new ArrayList<>(members);
    }

    public void addMember(Athlete athlete) {
        Objects.requireNonNull(athlete, "athlete must not be null");
        this.members.add(athlete);
    }

    public List<Stroke> getStrokeOrder() {
        return Collections.unmodifiableList(strokeOrder);
    }

    public void setStrokeOrder(List<Stroke> strokeOrder) {
        this.strokeOrder = strokeOrder == null ? new ArrayList<>() : new ArrayList<>(strokeOrder);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof RelayTeam)) return false;
        RelayTeam other = (RelayTeam) obj;
        return Objects.equals(teamName, other.teamName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamName);
    }

    @Override
    public String toString() {
        return "RelayTeam{" +
                "teamName='" + teamName + '\'' +
                ", members=" + members +
                ", strokeOrder=" + strokeOrder +
                '}';
    }
}
