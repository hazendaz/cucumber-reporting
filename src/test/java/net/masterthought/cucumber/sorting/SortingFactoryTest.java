package net.masterthought.cucumber.sorting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.powermock.reflect.Whitebox;

import net.masterthought.cucumber.generators.integrations.PageTest;
import net.masterthought.cucumber.json.Feature;
import net.masterthought.cucumber.json.support.StepObject;
import net.masterthought.cucumber.json.support.TagObject;

/**
 * @author Damian Szczepanik (damianszczepanik@github)
 */
class SortingFactoryTest extends PageTest {

    @BeforeEach
    void setUp() {
        setUpWithJson(SAMPLE_JSON);
    }

    @Test
    void sortFeatures_OnNATURAL_ReturnsSameList() {

        // given
        SortingFactory sortingFactory = new SortingFactory(SortingMethod.NATURAL);

        // when
        List<Feature> featureList = sortingFactory.sortFeatures(features);

        // then
        assertThat(featureList).containsExactly(features.get(0), features.get(1));
    }

    @Test
    void sortFeatures_OnALPHABETICAL_ReturnsSortedList() {

        // given
        SortingFactory sortingFactory = new SortingFactory(SortingMethod.ALPHABETICAL);

        // when
        List<Feature> featureList = sortingFactory.sortFeatures(features);

        // then
        assertThat(featureList).containsExactly(features.get(0), features.get(1));
    }

    @Test
    void sortFeatures_OnINVALID_ThrowsException() {

        // given
        // INVALID is available only for test profile and the reason of this shadow Enum in test profile is
        // to be able to test default: block which throws an exception for unsupported values
        SortingFactory sortingFactory = new SortingFactory(SortingMethod.INVALID);

        // when & then
        assertThatThrownBy(() -> sortingFactory.sortFeatures(features))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(buildErrorMessage());
    }

    @Test
    void sortTags_OnNATURAL_ReturnsSameList() {

        // given
        SortingFactory sortingFactory = new SortingFactory(SortingMethod.NATURAL);

        // when
        List<TagObject> tagObjects = sortingFactory.sortTags(tags);

        // then
        // TODO: as the tags are stored in TreeMap, this returns already sorted elements
        assertThat(tagObjects).containsExactly(tags.get(0), tags.get(1), tags.get(2));
    }

    @Test
    void sortTags_OnALPHABETICAL_ReturnsSameList() {

        // given
        SortingFactory sortingFactory = new SortingFactory(SortingMethod.ALPHABETICAL);

        // when
        List<TagObject> tagObjects = sortingFactory.sortTags(tags);

        // then
        assertThat(tagObjects).containsExactly(tags.get(0), tags.get(1), tags.get(2));
    }

    @Test
    void sortTags_OnINVALID_ThrowsException() {

        // given
        SortingFactory sortingFactory = new SortingFactory(SortingMethod.INVALID);

        // when & then
        assertThatThrownBy(() -> sortingFactory.sortTags(tags))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(buildErrorMessage());
    }

    @Test
    void sortSteps_OnNATURAL_ReturnsSameList() {

        // given
        SortingFactory sortingFactory = new SortingFactory(SortingMethod.NATURAL);

        // when
        List<StepObject> stepObjects = sortingFactory.sortSteps(steps);

        // then
        assertThat(stepObjects).hasSize(16).first().isEqualTo(steps.get(0));
        assertThat(stepObjects).last().isEqualTo(steps.get(15));
    }

    @Test
    void sortSteps_OnALPHABETICAL_ReturnsSameList() {

        // given
        SortingFactory sortingFactory = new SortingFactory(SortingMethod.ALPHABETICAL);

        // when
        List<StepObject> stepObjects = sortingFactory.sortSteps(steps);

        // then
        // TODO: as the tags are stored in TreeMap, this returns already sorted elements
        assertThat(stepObjects).hasSize(16).first().isEqualTo(steps.get(0));
        assertThat(stepObjects).last().isEqualTo(steps.get(15));
    }

    @Test
    void sortSteps_OnINVALID_ThrowsException() {

        // given
        SortingMethod sortingMethod = SortingMethod.INVALID;
        SortingFactory sortingFactory = new SortingFactory(sortingMethod);

        // when & then
        assertThatThrownBy(() -> sortingFactory.sortSteps(steps))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(buildErrorMessage());
    }

    @Test
    void createUnknownMethodException_CreatesException() throws Exception {

        // given
        SortingMethod invalidSorthingMethod = SortingMethod.ALPHABETICAL;
        SortingFactory sortingFactory = new SortingFactory(SortingMethod.ALPHABETICAL);

        // when
        Exception e = Whitebox.invokeMethod(sortingFactory, "createUnknownMethodException", invalidSorthingMethod);

        // then
        assertThat(e).isExactlyInstanceOf(IllegalArgumentException.class).hasMessage("Unsupported sorting method: " + invalidSorthingMethod);
    }

    private String buildErrorMessage() {
        return "Unsupported sorting method: " + SortingMethod.INVALID;
    }
}