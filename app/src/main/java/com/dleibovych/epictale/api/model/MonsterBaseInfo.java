package com.dleibovych.epictale.api.model;

import com.dleibovych.epictale.api.dictionary.Archetype;
import com.dleibovych.epictale.api.dictionary.MapCellTerrain;
import com.dleibovych.epictale.api.dictionary.MonsterType;
import com.dleibovych.epictale.api.dictionary.Skill;
import com.dleibovych.epictale.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Hamster
 * @since 25.01.2015
 */
public class MonsterBaseInfo {

    public final String name;
    public final int level;
    public final MonsterType type;
    public final Archetype archetype;
    public final Collection<Skill> skills;
    public final Collection<MapCellTerrain> area;
    public final List<ArtifactBaseInfo> artifacts;
    public final List<ArtifactBaseInfo> junk;
    public final String description;

    public MonsterBaseInfo(final String name, final String level, final String type, final String archetype,
                           final String skills, final String area, final List<ArtifactBaseInfo> artifacts,
                           final List<ArtifactBaseInfo> junk, final String description) {
        this.name = name;
        this.level = Integer.parseInt(level);
        this.type = ObjectUtils.getEnumForCode(MonsterType.class, type);
        this.archetype = ObjectUtils.getEnumForCode(Archetype.class, archetype);
        this.artifacts = artifacts;
        this.junk = junk;
        this.description = description;

        this.skills = new ArrayList<>();
        final String[] skillItems = skills.split(",\\s+");
        for(final String skillItem : skillItems) {
            this.skills.add(ObjectUtils.getEnumForName(Skill.class, skillItem));
        }

        this.area = new ArrayList<>();
        final String[] areas = area.split(",\\s+");
        for(final String areasItem : areas) {
            this.area.add(ObjectUtils.getEnumForCode(MapCellTerrain.class, areasItem));
        }
    }

}
