package com.dleibovych.epictale.api.model;

import com.dleibovych.epictale.api.dictionary.ArtifactEffect;
import com.dleibovych.epictale.api.dictionary.ArtifactOrientation;
import com.dleibovych.epictale.api.dictionary.ArtifactType;
import com.dleibovych.epictale.util.ObjectUtils;

/**
 * @author Hamster
 * @since 25.01.2015
 */
public class ArtifactBaseInfo {

    public final String name;
    public final int level;
    public final ArtifactType type;
    public final ArtifactOrientation orientation;
    public final ArtifactEffect effectRare;
    public final ArtifactEffect effectEpic;
    public final ArtifactEffect effectSpecial;
    public final String description;

    public ArtifactBaseInfo(final String name, final String level, final String type, final String orientation,
                            final String effectRare, final String effectEpic, final String effectSpecial,
                            final String description) {
        this.name = name;
        this.level = Integer.parseInt(level);
        this.type = ObjectUtils.getEnumForName(ArtifactType.class, type);
        this.orientation = ObjectUtils.getEnumForCode(ArtifactOrientation.class, orientation);
        this.effectRare = getArtifactEffectForName(effectRare);
        this.effectEpic = getArtifactEffectForName(effectEpic);
        this.effectSpecial = getArtifactEffectForName(effectSpecial);
        this.description = description;
    }

    private ArtifactEffect getArtifactEffectForName(final String name) {
        final ArtifactEffect artifactEffect = ObjectUtils.getEnumForName(ArtifactEffect.class, name);
        return artifactEffect == null ? ArtifactEffect.NO_EFFECT : artifactEffect;
    }

}
