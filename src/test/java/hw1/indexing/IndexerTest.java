package hw1.indexing;

import hw1.indexing.datareader.TextSanitizer;
import hw2.indexing.Indexer;
import junit.framework.TestCase;
import org.junit.Assert;

/**
 * Created by Abhishek Mulay on 6/2/17.
 */
public class IndexerTest extends TestCase {

    final String documentId = "AP890101-0001";
    final String text = "The celluloid torch has been passed to a new\n" +
            "generation: filmmakers who grew up in the 1960s.\n" +
            "   ``Platoon,'' ``Running on Empty,'' ``1969'' and ``Mississippi\n" +
            "Burning'' are among the movies released in the past two years from\n" +
            "writers and directors who brought their own experiences of that\n" +
            "turbulent decade to the screen.\n" +
            "   ``The contemporaries of the '60s are some of the filmmakers of\n" +
            "the '80s. It's natural,'' said Robert Friedman, the senior vice\n" +
            "president of worldwide advertising and publicity at Warner Bros.\n" +
            "   Chris Gerolmo, who wrote the screenplay for ``Mississippi\n" +
            "Burning,'' noted that the sheer passage of time has allowed him and\n" +
            "others to express their feelings about the decade.\n" +
            "   ``Distance is important,'' he said. ``I believe there's a lot of\n" +
            "thinking about that time and America in general.''\n" +
            "   The Vietnam War was a defining experience for many people in the\n" +
            "'60s, shattering the consensus that the United States had a right,\n" +
            "even a moral duty to intervene in conflicts around the world. Even\n" +
            "today, politicians talk disparagingly of the ``Vietnam Syndrome'' in\n" +
            "referring to the country's reluctance to use military force to\n" +
            "settle disputes.\n" +
            "   ``I think future historians will talk about Vietnam as one of the\n" +
            "near destructions of American society,'' said Urie Brofenbrenner, a\n" +
            "professor of sociology at Cornell University.\n" +
            "   ``In World War II, we knew what we were fighting for, but not in\n" +
            "Vietnam.''\n" +
            "   ``Full Metal Jacket,'' ``Gardens of Stone,'' ``Platoon,'' ``Good\n" +
            "Morning, Vietnam,'' ``Hamburger Hill'' and ``Bat 21'' all use the\n" +
            "war as a dramatic backdrop and show how it shaped characters' lives.\n" +
            "   The Vietnam War has remained an emotional issue in the United\n" +
            "States as veterans have struggled to come to terms with their\n" +
            "experiences. One was Oliver Stone, who wrote and directed the\n" +
            "Academy Award-winning ``Platoon.''\n" +
            "   ``I saw `Platoon' eight times,'' said John J. Anderson, a Palm\n" +
            "Beach County sheriff's lieutenant who served in Vietnam in 1966-67.\n" +
            "``I cried the first time I saw it ... and the third and fourth\n" +
            "times. `Platoon' helped me understand.''\n" +
            "   Stone, who based ``Platoon'' on some of his own experiences as a\n" +
            "grunt, said the film brought up issues that had yet to be resolved.\n" +
            "   ``People are responding to the fact that it's real. They're\n" +
            "curious about the war in Vietnam after 20 years,'' he said.\n" +
            "   While Southeast Asia was the pivotal foreign issue in American\n" +
            "society of the '60s, civil rights was the major domestic issue. The\n" +
            "civil rights movement reached its peak in the ``Freedom Summer'' of\n" +
            "1964, when large groups of volunteers headed South to help register\n" +
            "black voters.\n" +
            "   In ``Five Corners,'' a movie about the summer of '64 in the Bronx\n" +
            "starring Jodie Foster, her friend, played by Tim Robbins, leaves his\n" +
            "neighborhood to volunteer in the South after seeing the Rev. Martin\n" +
            "Luther King Jr. on television.\n" +
            "   Alan Parker's ``Mississippi Burning'' focuses on an incident that\n" +
            "clouded the Mississippi Summer Project _ when 1,000 young volunteers\n" +
            "from mainstream America swept into the state to help register black\n" +
            "voters. The movie is a fictionalized account of the disappearance\n" +
            "and slaying of three civil rights workers: Michael Schwerner, Andrew\n" +
            "Goodman and James Chaney.\n" +
            "   They were reported missing on June 21, several hours after being\n" +
            "stopped for speeding near Philadelphia, Miss. After a nationally\n" +

            "publicized search, their bodies were discovered Aug. 4 on a farm\n" +
            "just outside the town.\n" +
            "   One of those who recalled the incident was Gerolmo, a student in\n" +
            "the New York public school system at the time. The screenwriter said\n" +
            "the incident had a powerful effect on his way of thinking.\n" +
            "   ``It was the first time I ever considered that our country could\n" +
            "be wrong,'' Gerolmo said.\n" +
            "   The film stars Willem Dafoe and Gene Hackman star as FBI agents\n" +
            "who try to find the bodies of the missing workers and overcome\n" +
            "fierce local resistance to solve the crime.\n" +
            "   In a more offbeat and outrageous way, John Waters' ``Hairspray''\n" +
            "discusses integration in Baltimore in 1963 when a group of\n" +
            "teen-agers tries to break down the barriers of a segregated dance\n" +
            "show.\n" +
            "   Also set in Baltimore is Barry Levinson's ``Tin Men,'' starring\n" +
            "Danny DeVito and Richard Dreyfuss as two slick aluminum siding\n" +
            "salesmen in the early '60s. The movie mirrored a squarely\n" +
            "middle-class culture, one that was not caught up in sex, politics\n" +
            "and drugs.\n" +
            "   Instead of focusing on a well-known historic event,\n" +
            "writer-director Ernest Thompson takes a more personal approach in\n" +
            "``1969.'' Robert Downey Jr. and Keifer Sutherland star as college\n" +
            "students who battle their parents and each other over sex, drugs and\n" +
            "the Vietnam War.\n" +
            "   ``I was 19 in 1969. It was a fulcrum time for me,'' said\n" +
            "Thompson, who was a student at American University at the time. ``I\n" +
            "think it was just the right time in my growth as an artist and as a\n" +
            "man to try to write about something that happened in my youth.''\n" +
            "   ``Running on Empty'' takes place in the '80s but the '60s are\n" +
            "much in evidence. Judd Hirsch and Christine Lahti play anti-war\n" +
            "activists who sabatoged a napalm plant in 1970 and are forced to\n" +
            "live underground with their two children.\n" +
            "   Naomi Foner, who wrote ``Running on Empty'' and also served as\n" +
            "the film's executive producer, grew up in Brooklyn, N.Y., the\n" +
            "daughter of sociologists. Her own experiences made Foner well\n" +
            "qualified to give ``Running on Empty'' its strong political theme.\n" +
            "   ``I lived through that time and I've wanted to find the right way\n" +
            "to present it to this generation,'' said Foner, a member of the\n" +
            "radical Students for a Democratic Society while attending graduate\n" +
            "school at Columbia University.\n" +
            "   Foner, who also taught in Harlem's Head Start program and helped\n" +
            "register voters in South Carolina, said many young people are\n" +
            "curious about what happened in the '60s.\n" +
            "   ``A lot of them think it was an exciting time that they were\n" +
            "sorry to have missed,'' she said.\n" +
            "   Brofenbrenner said movies are a good indicator of the concerns of\n" +
            "the general public: ``The principle impact of the media is that they\n" +
            "reflect the values of the larger society.\n" +
            "   ``Film is a very powerful art medium,'' he said. ``I believe it\n" +
            "very accurately reflects not only the prevailing but the coming\n" +
            "trends. It's because film writers, like other writers, are\n" +
            "perceptive people. They get the message of what's going on.''";

    public void setUp() throws Exception {
        super.setUp();
    }

    public void testGetTermFrequencyinText() throws Exception {
        String[] tokens = TextSanitizer.tokenize(text, true);
        String testTerm = "celluloid";
        String notPresentTerm = "Abhishek";
        double termFrequencyinText = Indexer.getTermFrequencyinText(testTerm, tokens);
        double notPresentTf = Indexer.getTermFrequencyinText(notPresentTerm, tokens);
        System.out.println(termFrequencyinText);
        Assert.assertTrue(1.0 == termFrequencyinText);
        Assert.assertTrue(0.0 == notPresentTf);
    }

}