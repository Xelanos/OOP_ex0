

/**
 * A class representing an Assembly.
 */
public class Assembly {

    int lawCapacity, maxLawPerKnessetMember, numberOfCurrentLaws;
    Law[] lawArray;
    int NEGATIVE = -1;
    int maxKnessetMembers = 120;
    KnessetMember[] membersArray = new KnessetMember[maxKnessetMembers];

    /**
     * Creates a new assembly with the given parameters.
     * @param maxLawCapacity The maximal number of laws this assembly can hold.
     * @param maxSupportedLawsPerKnessetMember The maximal number of laws this assembly allows a single KnessetMember to support at the
     * same time.
     */
    Assembly(int maxLawCapacity, int maxSupportedLawsPerKnessetMember){
        lawCapacity = maxLawCapacity;
        maxLawPerKnessetMember = maxSupportedLawsPerKnessetMember;
        numberOfCurrentLaws = 0;
        lawArray = new Law[lawCapacity];

    }


    /**
     * Adds the given law to this assembly, if there is place available, and it isn't already in the assembly.
     * @param law The law to add to this assembly.
     * @param surveyResult The survey result of the law.
     * @return a non-negative id number for the law if there was a spot and the law was successfully
     * added, or if the law was already in the assembly; a negative number otherwise.
     */
    int addLawToAssembly(Law law, int surveyResult){
        law.surveyResult = surveyResult;
        for (int i=0; i < lawCapacity; i++){
            if (lawArray[i]== law){
                return i;
            }else if (lawArray[i] == null){
                lawArray[i]= law;
                return i;
            }
        }
        return NEGATIVE;
    }


    /**
     * updates the survey result of lawId with a new value
     * @param law law to be updated
     * @param newSurveyValue new survey value.
     */
    void updateSurveyResultOfLaw(Law law, int newSurveyValue){
        law.surveyResult = newSurveyValue;
    }


    /**
     * Returns true if the given number is an id of some law in the assembly, false otherwise.
     * @param lawId The id to check.
     * @return true if the given number is an id of some law in the assembly, false otherwise.
     */
    boolean isLawIDValid(int lawId) {
        return lawId >= 0 && lawArray[lawId] != null;
    }


    /**
     * Returns the non-negative id number of the given law if he is discussed by this assembly, -1 otherwise.
     * @param law The law for which to find the id number.
     * @return a non-negative id number of the given law if he is discussed by this assembly, -1 otherwise.
     */
    int getLawId(Law law){
        for (int i=0; i < lawCapacity; i++){
            if (lawArray[i] == law)
                return i;
        }
        return NEGATIVE;
    }


    /**
     * Registers the given KnessetMember to this assembly, if there is a spot available.
     * @param KnessetMember The KnessetMember to register to this assembly.
     * @return a non-negative id number for the KnessetMember if there was a spot and the patron was successfully
     * registered, a negative number otherwise.
     */
    int registerKnessetMember(KnessetMember KnessetMember){
        for (int i=0; i < lawCapacity; i++){
            if (membersArray[i]== KnessetMember){
                return i;
            }else if (membersArray[i] == null){
                membersArray[i]= KnessetMember;
                return i;
            }
        }
        return NEGATIVE;
    }


    /**
     * Returns true if the given number is an id of a KnessetMember in the assembly, false otherwise.
     * @param KnessetMemberId The id to check.
     * @return  true if the given number is an id of a KnessetMember in the assembly, false otherwise.
     */
    boolean isKnessetMemberIdValid(int KnessetMemberId){
        return KnessetMemberId >= 0 && membersArray[KnessetMemberId] != null;

    }


    /**
     * Returns the non-negative id number of the given KnessetMember if he is registered to this assembly, -1 otherwise.
     * @param KnessetMember The KnessetMember for which to find the id number.
     * @return a non-negative id number of the given KnessetMember if he is registered to this assembly, -1 otherwise.
     */
    int getKnessetMemberId(KnessetMember KnessetMember){
        for (int i=0; i < maxKnessetMembers; i++){
            if (membersArray[i]==KnessetMember)
                return i;
        }
        return NEGATIVE;
    }


    /**
     * adds KnessetMember to law, if the KnessetMember will support the law according to the survey results.
     * @param lawId The id number of the law to borrow.
     * @param KnessetMemberId The id number of the KnessetMember that will support the law.
     * @param surveyResult The survey result of the law to support.
     * @return true if the KnessetMember was added successfully, false otherwise.
     */
    boolean supportLaw(int lawId, int KnessetMemberId, int surveyResult){
        if (isLawIDValid(lawId) && isKnessetMemberIdValid(KnessetMemberId)){
            Law law = lawArray[lawId];
            KnessetMember knessetMember = membersArray[KnessetMemberId];
            if (knessetMember.willJoinLaw(law,law.surveyResult)&&
                    knessetMember.numberOfLawsSupported <= maxLawPerKnessetMember){
                law.addJoinedKnessetMember();
                knessetMember.numberOfLawsSupported++;
                return true;
            } else return false;
        } else return false;
    }

    /**
     * Get the ID of the law with the most score in regards to the respectable KM, out of the laws currently
     * discussed at the assembly
     * @param knessetMember KM in which to calculate max score to
     * @return an ID of the law with the most score in regards to the respectable KM; return -1 if all laws got 0.
     */
    int getMaxScoreIndex(KnessetMember knessetMember) {
        double maxScore = 0;
        int maxIndex = NEGATIVE;
        for (int i = 0; i < lawCapacity; i++) {
            Law law = lawArray[i];
            if (law == null)
                break;
            double lawScore = knessetMember.getLawScore(law, law.surveyResult);
            if (lawScore > maxScore) {
                maxScore = lawScore;
                maxIndex = i;
            }
        }
        return maxIndex;
        }


    /**
     * Suggest to the KnessetMember with the given id a law which suits him the best (maximum score of the laws in the assembly).
     * @param KnessetMemberId The id number of the KnessetMember to suggest the law to.
     * @return The best law to match the KnessetMember preferences. Null if there aren't any (case all laws get a zero score).
     * available.
     */
    Law suggestLawToKnessetMember(int KnessetMemberId){
        if (isKnessetMemberIdValid(KnessetMemberId)){
            KnessetMember knessetMember = membersArray[KnessetMemberId];
            int maxLawIndex = getMaxScoreIndex(knessetMember);
            if (maxLawIndex == NEGATIVE){
                return null;
            }else return lawArray[maxLawIndex];

            } else return null;
   }

}
