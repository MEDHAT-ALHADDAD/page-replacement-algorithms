package page.replacement.algorithms;

import java.util.*;

class ReferenceCall { //Each object is a unique call, everything is stored in an arraylist, due to its dynamic size, it may not be the most efficient(Array expansion is costly) but it's easy to work with.

    public int rCall, FIFO_time, LRU_LastUsage, LFU_nUsage, SC_rBit, ESC_rBit, ESC_mBit, optimal_nFutureUsage;
}
//Lama el tananeen tcode mt3edesh warahom homa katabo kam kilo line of code.

public class OSProject2{

    public static int pageFault, pageCounter, frameCounter;
    public static ReferenceCall[] inMem;

    public static void printRString(ArrayList<ReferenceCall> calls) { //Prints the reference string.
        int i;
        System.out.print("Reference String: ");
        for (i = 0; i < calls.size() - 1; i++) {
            System.out.print((calls.get(i)).rCall + ",");
        }
        System.out.println(calls.get(i).rCall);
    }

    public static void printArr(ReferenceCall[] arr) { //Prints the array horizontally.
        int i;
        for (i = 0; i < arr.length - 1; i++) {
            System.out.print(arr[i].rCall + ",");
        }
        System.out.println(arr[i].rCall);
    }

    public static void printArrPlusRBit(ReferenceCall[] arr) {//Prints the array and the R bit table vertically.
        int i;
        System.out.println("Pages in Memory: \tReference Bit: ");
        for (i = 0; i < arr.length; i++) {
            System.out.println("\t\t" + arr[i].rCall + "\t\t\t" + arr[i].SC_rBit);
            System.out.println();
        }
    }

    public static void printArrPlusRBitPlusMBit(ReferenceCall[] arr) {//Prints the array and the R bit and M bit tables vertically.
        int i;
        System.out.println("Pages in Memory: \tReference Bit: \t\tModification Bit: ");
        for (i = 0; i < arr.length; i++) {
            System.out.println("\t\t" + arr[i].rCall + "\t\t\t" + arr[i].ESC_rBit + "\t\t\t" + arr[i].ESC_mBit);
            System.out.println();
        }
    }

    public static int hasNum(ReferenceCall[] arr, int x) { //Checks for a certain number x in array arr, if x exists it returns the index of x, if x does not exist -1 is returned
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].rCall == x) {
                return i;
            }
        }
        return -1;
    }

    public static boolean hasNumBool(ReferenceCall[] arr, int x) { //Not working for some unknown reason
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].rCall == x) {
                return true;
            }
        }
        return false;
    }

    //Wala t2oly tuple wala pair, ana el code bta3y maby3mlsh 7aga gheer eno byfail
    public static void initFill(ArrayList<ReferenceCall> calls, int nFrames) {
        inMem = new ReferenceCall[nFrames];
        for (int i = 0; i < inMem.length; i++) {
            inMem[i] = new ReferenceCall();
            inMem[i].rCall = -1;
            inMem[i].LRU_LastUsage = 1;
        }
        //The loop keeps on working untill inMem has no zeroes, which happens when it is filled.
        while (hasNum(inMem, -1) > -1 && pageCounter < calls.size()) { //frameCounter is used as a counter for memory frames, pageCounter is the counter for requested pages. la ta5rog qabl an taqool sob7an allah
            if (hasNum(inMem, calls.get(pageCounter).rCall) == -1) {//Missing condition
                inMem[frameCounter] = calls.get(pageCounter);
                pageFault++;
                for (int j = frameCounter; j >= 0; j--) {
                    inMem[j].FIFO_time++;
                }
                for (int k = frameCounter; k >= 0; k--) {
                    inMem[k].LRU_LastUsage++;
                }
                inMem[frameCounter].LFU_nUsage = 1;
                frameCounter++;
            } else {
                int repeatedCall = hasNum(inMem, calls.get(pageCounter).rCall);
                inMem[repeatedCall].LFU_nUsage++;
                inMem[repeatedCall].SC_rBit = 1;
            }
            pageCounter++;
        }
    }

    public static int FIFO(ArrayList<ReferenceCall> calls) {
        int LTVind = 0, FIFOPageFault = pageFault, FIFOPageCounter = pageCounter; //Largest time value index
        //Making a separate memory array for FIFO
        ReferenceCall[] FIFOMem = new ReferenceCall[inMem.length];
        for (int arrInd = 0; arrInd < FIFOMem.length; arrInd++) {
            FIFOMem[arrInd] = inMem[arrInd];
        }
        //------------------------------------------------------

        for (int i = FIFOPageCounter; i < calls.size(); i++) {  //Number of iterations= Remaining Calls, Remaining Calls= total n calls - pagecounter, pagecounter=the pages that have been finished by the initial fill method.
            if (!hasNumBool(FIFOMem, -1)) { //Double checking on the initial fill method
                for (int j = 0; j < FIFOMem.length; j++) {
                    if (FIFOMem[j].FIFO_time > FIFOMem[LTVind].FIFO_time) {
                        LTVind = j;
                    }
                }
                if (hasNum(FIFOMem, calls.get(FIFOPageCounter).rCall) == -1) { //Repitition Condition
                    FIFOMem[LTVind] = calls.get(FIFOPageCounter);
                    for (int l = 0; l < FIFOMem.length; l++) {
                        FIFOMem[l].FIFO_time++;
                    }
                    FIFOMem[LTVind].FIFO_time = 1;
                    FIFOPageFault++;
                }
                printArr(FIFOMem);
                FIFOPageCounter++;
            }
        }
        System.out.println("Page Faults:" + FIFOPageFault);
        return FIFOPageFault;
    }

    public static int LRU(ArrayList<ReferenceCall> calls) {
        int furthestRefInd = 0, repeatedCallInd = 0, LRUPageFault = pageFault, LRUPageCounter = pageCounter;
        //Making a separate memory array for LRU
        ReferenceCall[] LRUMem = new ReferenceCall[inMem.length];
        for (int arrInd = 0; arrInd < LRUMem.length; arrInd++) {
            LRUMem[arrInd] = inMem[arrInd];
        }
        //-----------------------------------------------------

        for (int i = LRUPageCounter; i < calls.size(); i++) { //Number of iterations= Remaining Calls, Remaining Calls= total n calls - pagecounter, pagecounter=the pages that have been finished by the initial fill method.
            if (!hasNumBool(LRUMem, -1)) {
                for (int j = 0; j < LRUMem.length; j++) {
                    if (LRUMem[j].LRU_LastUsage > LRUMem[furthestRefInd].LRU_LastUsage) {
                        furthestRefInd = j;
                    }
                }
                if (hasNum(LRUMem, calls.get(LRUPageCounter).rCall) == -1) {
                    LRUMem[furthestRefInd] = calls.get(LRUPageCounter);
                    for (int l = 0; l < LRUMem.length; l++) {
                        LRUMem[l].LRU_LastUsage++;
                    }
                    LRUMem[furthestRefInd].LRU_LastUsage = 1;
                    LRUPageFault++;
                } else {
                    repeatedCallInd = hasNum(LRUMem, calls.get(LRUPageCounter).rCall);
                    LRUMem[repeatedCallInd].LRU_LastUsage = 1;
                }
                printArr(LRUMem);
                LRUPageCounter++;
            }
        }
        System.out.println("Page Faults:" + LRUPageFault);
        return LRUPageFault;
    }

    public static int LFU(ArrayList<ReferenceCall> calls) {
        int SUInd = 0, repeatedCallInd = 0, lastVictimInd = -1, LFUPageFault = pageFault, LFUPageCounter = pageCounter;//Smallest Usage Index
        //Making a separate memory for LRU
        ReferenceCall[] LFUMem = new ReferenceCall[inMem.length];
        for (int arrInd = 0; arrInd < LFUMem.length; arrInd++) {
            LFUMem[arrInd] = inMem[arrInd];
        }
        //-----------------------------------------------------

        for (int i = LFUPageCounter; i < calls.size(); i++) { //Number of iterations= Remaining Calls, Remaining Calls= total n calls - pagecounter, pagecounter=the pages that have been finished by the initial fill method.
            if (!hasNumBool(LFUMem, -1)) {
                if (lastVictimInd >= LFUMem.length) {
                    lastVictimInd = lastVictimInd % LFUMem.length; //Circulation
                }
                for (int j = lastVictimInd + 1; j < LFUMem.length; j++) {
                    if (LFUMem[j].LFU_nUsage < LFUMem[SUInd].LFU_nUsage || lastVictimInd == SUInd) { //LastVictimInd is used to circulate through if there are no repeated references, ex: 10 20 30 40 50 60 70 80
                        //without the condition, it will be 50 20 30 40, 60 20 30 40, etc.
                        SUInd = j;
                    }
                }
                if (hasNum(LFUMem, calls.get(LFUPageCounter).rCall) == -1) {
                    LFUMem[SUInd] = calls.get(LFUPageCounter);
                    LFUMem[SUInd].LFU_nUsage = 1;
                    lastVictimInd = SUInd;
                    LFUPageFault++;
                } else {
                    repeatedCallInd = hasNum(LFUMem, calls.get(LFUPageCounter).rCall);
                    LFUMem[repeatedCallInd].LFU_nUsage++;
                }
                printArr(LFUMem);
                LFUPageCounter++;
            }
        }
        System.out.println("Page Faults:" + LFUPageFault);
        return LFUPageFault;
    }

    public static int secondChance(ArrayList<ReferenceCall> calls) { //el bta3a di feeha 7owar 3shan lama tsheel wa7da temshy men ely ba3deeha 3alatool, 3shan keda fi remainder lel lastvictim
        int rBit0Ind = 0, repeatedCallInd = 0, lastVictimInd = -1, SCPageFault = pageFault, SCPageCounter = pageCounter;
        //Making a separate memory for SC
        ReferenceCall[] SCMem = new ReferenceCall[inMem.length];
        for (int arrInd = 0; arrInd < SCMem.length; arrInd++) {
            SCMem[arrInd] = inMem[arrInd];
        }
        //----------------------------------------------------

        for (int i = SCPageCounter; i < calls.size(); i++) {  //Number of iterations= Remaining Calls, Remaining Calls= total n calls - pagecounter, pagecounter=the pages that have been finished by the initial fill method.
            if (lastVictimInd >= SCMem.length) {
                lastVictimInd = lastVictimInd % SCMem.length; //Remainder incase the last victim was the last page in the memory, without this, if the last victim is the last page in memory then the loop won't ever execute.
            }
            while (rBit0Ind == -1) {
                for (int j = lastVictimInd + 1; j < SCMem.length; j++) {//Start to check the pages with rbit=0 right after the last page replaced(lastVictim)
                    if (SCMem[j].SC_rBit == 0) {
                        rBit0Ind = j;
                        break;
                    } else {
                        rBit0Ind = -1;
                    }
                }
                if (rBit0Ind == -1) {//The first loop is used to go from lastvictim +1 till end, if a 0bit is still not found, this coming loop goes from the start till lastvictim-1(Circulation men el a5er)
                    for (int k = 0; k < lastVictimInd - 1; k++) {
                        if (SCMem[k].SC_rBit == 0) {
                            rBit0Ind = k;
                            break;
                        } else {
                            rBit0Ind = -1;
                        }
                    }
                }
                if (rBit0Ind == -1 || lastVictimInd == SCMem.length) { //Didn't find a reference bit that is equal to zero, resets all rbits to zero. the second condition is useless not sure if it should be removed or kept for safety.
                    for (int j = 0; j < SCMem.length; j++) {
                        SCMem[j].SC_rBit = 0;
                    }
                }
            }
            if (!hasNumBool(SCMem, -1)) { //Double checking on the initial fill method
                if ((hasNum(SCMem, calls.get(SCPageCounter).rCall) == -1) && SCPageCounter < calls.size() && rBit0Ind < SCMem.length && rBit0Ind != -1) { //Repitition Condition, the part after the logical and is for the exception rBit0Ind<SCMem.length
                    SCMem[rBit0Ind] = calls.get(SCPageCounter); //ArrayIndexOutOfBounds: -1 exception -- -1 is an error code? or is it the index that is used and is out of bounds? -- Logic re-evaluation ya abdallah
                    SCMem[rBit0Ind].SC_rBit = 0; //Useless but it is here as a safety check.
                    lastVictimInd = rBit0Ind;//lastVictim points at the last replaced page so we could start from the page right after.
                    SCPageFault++;
                } else {
                    repeatedCallInd = hasNum(SCMem, calls.get(SCPageCounter).rCall);
                    SCMem[repeatedCallInd].SC_rBit = 1;
                }

                SCPageCounter++;
                printArrPlusRBit(SCMem);
            }
        }
        System.out.println("Page Faults:" + SCPageFault);
        return SCPageFault;
    }

    public static int enhancedSC(ArrayList<ReferenceCall> calls) {
        int victimInd = -1, repeatedCallInd = 0, lastVictimInd = -1, ESCPageFault = pageFault, ESCPageCounter = pageCounter, startInd;
        //Making a separate memory for ESC
        ReferenceCall[] ESCMem = new ReferenceCall[inMem.length];
        for (int arrInd = 0; arrInd < ESCMem.length; arrInd++) {
            ESCMem[arrInd] = inMem[arrInd];
        }
        //-----------------------------------------------------

        for (int i = ESCPageCounter; i < calls.size(); i++) {  //Number of iterations= Remaining Calls, Remaining Calls= total n calls - pagecounter, pagecounter=the pages that have been finished by the initial fill method.
            victimInd = -1;
            if (lastVictimInd >= ESCMem.length) {
                lastVictimInd = lastVictimInd % ESCMem.length; //Remainder incase the last victim was the last page in the memory, without this, if the last victim is the last page in memory then the loop won't ever execute.
            }
            while (victimInd == -1) {//Loops on these two operations until a 0,0 is found or a 0,1    
                //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                //Trying to find (0,0)
                //Infinite Loop Fix -- Logic to be re-evaluated ya abdallah, and does SC need this too? the startIndex logic?
                startInd = lastVictimInd + 1;
                if (startInd >= ESCMem.length) {
                    startInd = startInd % ESCMem.length;
                }
                //----------------------------------

                if (startInd > ESCMem.length && ESCMem.length == 1)//One frame condition
                {
                    startInd--;
                }
                for (int j = startInd; j < ESCMem.length; j++) {//Start to check the pages with rbit=0 and mbit=0 right after the last page replaced(lastVictim)
                    if (ESCMem[j].ESC_rBit == 0 && ESCMem[j].ESC_mBit == 0) {
                        victimInd = j;
                        break;
                    } else {
                        victimInd = -1;
                    }
                }
                if (victimInd == -1) {//The first loop is used to go from lastvictim +1 till end, if a 0bit is still not found, this coming loop goes from the start till lastvictim-1(Circulation men el a5er)
                    for (int k = 0; k < lastVictimInd; k++) {//lastVictimInd or lastVictimInd-1? -- Logic to be re-evaluated ya abdallah
                        if (ESCMem[k].ESC_rBit == 0 && ESCMem[k].ESC_mBit == 0) {
                            victimInd = k;
                            break;
                        } else {
                            victimInd = -1;
                        }
                    }
                }
                //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                //Didn't find (0,0), trying to find (0,1), changing all rbits from 1 to 0.
                if (victimInd == -1) {
                    //Infinite Loop Fix -- Logic to be re-evaluated ya abdallah
                    if (startInd >= ESCMem.length) {
                        startInd = startInd % ESCMem.length;
                    }
                    if (startInd > ESCMem.length && ESCMem.length == 1)//One frame condition
                    {
                        startInd--;
                    }
                    for (int j = startInd; j < ESCMem.length; j++) {//Start to check the pages with rbit=0 and mbit=1 right after the last page replaced(lastVictim)
                        if (ESCMem[j].ESC_rBit == 0 && ESCMem[j].ESC_mBit == 1) {
                            victimInd = j;
                            break;
                        } else {
                            ESCMem[j].ESC_rBit = 0;
                            victimInd = -1;
                        }
                    }
                    if (victimInd == -1) {//The first loop is used to go from lastvictim +1 till end, if a 0bit is still not found, this coming loop goes from the start till lastvictim-1(Circulation men el a5er)
                        for (int k = 0; k < lastVictimInd + 1; k++) {//lastVictimInd or lastVictimInd-1?
                            if (ESCMem[k].ESC_rBit == 0 && ESCMem[k].ESC_mBit == 1) {
                                victimInd = k;
                                break;
                            } else {
                                ESCMem[k].ESC_mBit = 0;
                                victimInd = -1;
                            }
                        }
                    }
                }
            }
            if (!hasNumBool(ESCMem, -1)) { //Double checking on the initial fill method
                if ((hasNum(ESCMem, calls.get(ESCPageCounter).rCall) == -1)) { //Repitition Condition
                    ESCMem[victimInd] = calls.get(ESCPageCounter);
                    ESCMem[victimInd].SC_rBit = 0; //Useless but it is here as a safety check.
                    lastVictimInd = victimInd;//lastVictim points at the last replaced page so we could start from the page right after.
                    ESCPageFault++;
                } else {
                    repeatedCallInd = hasNum(ESCMem, calls.get(ESCPageCounter).rCall);
                    ESCMem[repeatedCallInd].ESC_rBit = 1;
                }

                ESCPageCounter++;
                printArrPlusRBitPlusMBit(ESCMem);
            }
        }
        System.out.println("Page Faults:" + ESCPageFault);
        return ESCPageFault;
    }

    public static int optimal(ArrayList<ReferenceCall> calls) {
        int FUIFInd = 0, lastVictimInd = -1, optPageFault = pageFault, optPageCounter = pageCounter; //Furthest Usage In Future Index, index of the page in the memory to be replaced(victim).
        //Making a separate memory for optimal
        ReferenceCall[] optMem = new ReferenceCall[inMem.length];
        for (int arrInd = 0; arrInd < optMem.length; arrInd++) {
            optMem[arrInd] = inMem[arrInd];
        }
        //-----------------------------------------------------

        for (int i = optPageCounter; i < calls.size(); i++) {  //Number of iterations= Remaining Calls, Remaining Calls= total n calls - pagecounter, pagecounter=the pages that have been finished by the initial fill method.
            if (!hasNumBool(optMem, -1)) { //Double checking on the initial fill method
                if (lastVictimInd >= optMem.length) {
                    lastVictimInd = lastVictimInd % optMem.length;
                }
                for (int j = lastVictimInd + 1; j < optMem.length; j++) {//two nested loops in order to check
                    for (int k = optPageCounter; k < calls.size(); k++) {
                        if (optMem[j].rCall == calls.get(k).rCall || lastVictimInd == FUIFInd) {
                            optMem[j].optimal_nFutureUsage = k; //lastVictimInd==FUIFInd is for circulation(3shan mayo3odsh yghyr nafs el page lw mfeesh ay repeated references
                        }
                    }
                }
                for (int l = 0; l < optMem.length; l++) {//This counter is a small leter L, too many loops, I know that there is some simplification to be done somewhere, but time is tight, what matters is that it works.
                    //After another thought, it probably doesn't work either, Whoops.
                    if (optMem[l].optimal_nFutureUsage > optMem[FUIFInd].optimal_nFutureUsage) {
                        FUIFInd = l;
                    }
                }
                if (hasNum(optMem, calls.get(optPageCounter).rCall) == -1) { //Repitition Condition
                    optMem[FUIFInd] = calls.get(optPageCounter);
                    lastVictimInd = FUIFInd; //Circulation
                    optPageFault++;
                }
                optPageCounter++;
                printArr(optMem);
            }
        }
        System.out.println("Page Faults:" + optPageFault);
        return optPageFault;
    }

    public static void main(String[] args) {
        ArrayList<ReferenceCall> rCalls;
        Scanner sc = new Scanner(System.in);
        Random r = new Random();
        int mode, nFrames, nPages, alg;
        int[] Faults = new int[6];
        System.out.println("Test Mode (1)\nNormal Mode(2)"); //Two execution modes, first one is made for Abdullah for testing. 
        mode = sc.nextInt();
        if (mode == 1) { //Code that allows us to test the code, Yep recursion can happen in comments too, what a surprise.
            rCalls = new ArrayList<ReferenceCall>(99);
            System.out.println("Number of Frames");
            nFrames = sc.nextInt();
//            System.out.println("Number of Pages");
//            nPages=sc.nextInt();//Useless, I have no idea why this is here, will remove it inshaallah when Abdullah finishes testing.
            System.out.println("Enter references (a5er wa7da -1 3shan to2af)");
            for (int i = 0; i < 99; i++) {
                int currentCall = sc.nextInt();
                if (currentCall == -1) {
                    break;
                }
                rCalls.add(new ReferenceCall());
                rCalls.get(i).rCall = currentCall;
            }
            rCalls.trimToSize();
            printRString(rCalls);
            System.out.println("(1) FIFO, (2) LRU, (3) LFU, (4) Second Chance, (5) Enhanced Second Chance, (6) Optimal");
            alg = sc.nextInt();
            initFill(rCalls, nFrames);
            System.out.println("When memory is filled: ");
            printArr(inMem);
            switch (alg) {
                case 1:
                    System.out.println("FIFO: ");
                    FIFO(rCalls);
                    break;
                case 2:
                    System.out.println("LRU: ");
                    LRU(rCalls);
                    break;
                case 3:
                    System.out.println("LFU: ");
                    LFU(rCalls);
                    break;
                case 4:
                    System.out.println("Second Chance: ");
                    secondChance(rCalls);
                    break;
                case 5:
                    System.out.println("Enter the modification bits");
                    for (int i = 0; i < rCalls.size(); i++) {
                        rCalls.get(i).ESC_mBit = sc.nextInt();
                    }
                    System.out.println("Enhanced Second Chance: ");
                    enhancedSC(rCalls);
                    break;
                case 6:
                    System.out.println("Optimal: ");
                    optimal(rCalls);
                    break;
                default:
                    System.out.println("Switching mode to FIFO automatically...");
                    System.out.println("FIFO: ");
                    FIFO(rCalls);
                    break;
            }
        }
        if (mode == 2) {
            nFrames = r.nextInt(20) + 1;
            System.out.println("Number of Frames: " + nFrames);
            nPages = r.nextInt(50);
            while (nPages <= nFrames) {
                nPages = r.nextInt(50);
            }
            System.out.println("Number of Pages: " + nPages);
            rCalls = new ArrayList<ReferenceCall>(nPages);
            for (int i = 0; i < nPages; i++) {
                rCalls.add(new ReferenceCall());
                int currentCall = r.nextInt(100);
                rCalls.get(i).rCall = currentCall;
            }
            printRString(rCalls);
            initFill(rCalls, nFrames);
            System.out.println("Randomizing Modification Bit...");
            for (int i = 0; i < rCalls.size(); i++) {
                rCalls.get(i).ESC_mBit = r.nextInt(2);
            }
            System.out.println("When Memory is Filled: ");
            printArr(inMem);
            System.out.println("FIFO: ");
            Faults[0] = FIFO(rCalls);
            System.out.println("LRU: ");
            Faults[1] = LRU(rCalls);
            System.out.println("LFU: ");
            Faults[2] = LFU(rCalls);
            System.out.println("Second Chance: ");
            Faults[3] = secondChance(rCalls);
            System.out.println("Enhanced Second Chance: ");
            Faults[4] = enhancedSC(rCalls);
            System.out.println("Optimal: ");
            Faults[5] = optimal(rCalls);
            int minFaultsInd = 0;
            for (int k = 0; k < 6; k++) {
                if (Faults[minFaultsInd] > Faults[k]) {
                    minFaultsInd = k;
                }
            }
            switch (minFaultsInd) {
                case 0:
                    System.out.println("The best algorithm for this reference string is FIFO, Page Faults: " + Faults[0]);
                    break;
                case 1:
                    System.out.println("The best algorithm for this reference string is LRU, Page Faults: " + Faults[1]);
                    break;
                case 2:
                    System.out.println("The best algorithm for this reference string is LFU, Page Faults: " + Faults[2]);
                    break;
                case 3:
                    System.out.println("The best algorithm for this reference string is Second Chance, Page Faults: " + Faults[3]);
                    break;
                case 4:
                    System.out.println("The best algorithm for this reference string is Enhanced Second Chance, Page Faults: " + Faults[4]);
                    break;
                case 5:
                    System.out.println("The best algorithm for this reference string is Optimal, Page Faults: " + Faults[5]);
                    break;
            }
        }
    }
}
