package com.example.demo.config;

import com.example.demo.model.AptitudeQuestion;
import com.example.demo.repository.AptitudeQuestionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AptitudeDataLoader {

    @Bean
    CommandLineRunner loadAptitudeData(AptitudeQuestionRepository repo) {
        return args -> {
            // Ensures the test suite works automatically upon initial spring boot compiling
            if (repo.count() == 0) {
                repo.saveAll(List.of(
                        // Quantitative
                        new AptitudeQuestion("quantitative",
                                "A train 125 m long passes a man, running at 5 km/hr in the same direction in which the train is going, in 10 seconds. The speed of the train is:",
                                "45 km/hr", "50 km/hr", "54 km/hr", "55 km/hr", "B",
                                "Relative speed = 125/10 = 12.5 m/s = 45 km/hr. Let speed of train be X. (X - 5) = 45. X = 50."),
                        new AptitudeQuestion("quantitative",
                                "The probability of getting a sum 9 from two throws of a dice is:", "1/6", "1/8", "1/9",
                                "1/12", "C",
                                "Possible pairs: (3,6), (4,5), (5,4), (6,3) = 4 pairs. Total outcome pairs = 36. 4/36 = 1/9."),
                        new AptitudeQuestion("quantitative",
                                "A sum of money at simple interest amounts to Rs. 815 in 3 years and to Rs. 854 in 4 years. The sum is:",
                                "Rs. 650", "Rs. 690", "Rs. 698", "Rs. 700", "C",
                                "S.I. for 1 year = (854 - 815) = 39. S.I. for 3 years = 39 * 3 = 117. Sum = 815 - 117 = 698."),
                        new AptitudeQuestion("quantitative",
                                "What is the least number which when divided by 5, 6, 7 and 8 leaves a remainder 3, but when divided by 9 leaves no remainder?",
                                "1677", "1683", "2523", "3363", "B",
                                "The LCM of (5, 6, 7, 8) is 840. Number is of the form 840k + 3. For k=2, 1683 is divisible by 9."),

                        // Logical Reasoning
                        new AptitudeQuestion("logical",
                                "Look at this series: 2, 1, (1/2), (1/4), ... What number should come next?", "(1/3)",
                                "(1/8)", "(2/8)", "(1/16)", "B",
                                "This is a simple division series; each number is exactly one-half of the previous number."),
                        new AptitudeQuestion("logical", "Which word does NOT belong with the others?", "Parsley",
                                "Basil", "Dill", "Mayonnaise", "D",
                                "Parsley, basil, and dill are types of classical herbs natively known. Mayonnaise is not an herb."),
                        new AptitudeQuestion("logical", "Odometer is to mileage as compass is to:", "speed", "hiking",
                                "needle", "direction", "D",
                                "An odometer is an instrument used to measure mileage. A compass is an instrument used to natively determine direction."),
                        new AptitudeQuestion("logical", "SCD, TEF, UGH, ____, WKL", "CMN", "UJI", "VIJ", "IJT", "C",
                                "The first letters follow alphabetical order: S, T, U, V, W. The second and third letters are pairs starting with C and D: CD, EF, GH, IJ, KL. Thus, VIJ."),

                        // Verbal Ability
                        new AptitudeQuestion("verbal", "Antonym of ENORMOUS", "Soft", "Average", "Tiny", "Weak", "C",
                                "Enormous strictly means very large in size, quantity, or extent. Tiny is the exact definitional opposite."),
                        new AptitudeQuestion("verbal", "Synonym of ABANDON", "Forsake", "Keep", "Cherish", "Enlarge",
                                "A",
                                "Abandon means to leave completely and finally; forsake is the most accurate classical synonym."),
                        new AptitudeQuestion("verbal", "Find the correctly spelt word.", "Adulation", "Adalation",
                                "Aduletian", "Addulation", "A",
                                "Adulation means obsequious flattery; excessive admiration or praise."),
                        new AptitudeQuestion("verbal", "Complete the sentence: He has been living here ________ 1998.",
                                "for", "since", "from", "until", "B",
                                "We always use 'since' strictly with point of time (1998 in this case) in perfect and perfect continuous grammatical tenses.")));
            }
        };
    }
}
