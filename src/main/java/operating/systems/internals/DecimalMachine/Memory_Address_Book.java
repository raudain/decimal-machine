package operating.systems.internals.DecimalMachine;

/**
 * This interface has the boundary pointers for the simulated system memory 
 * array along with a couple of program execution return codes.
 * 
 * @author Roody
 * @version 03 August 2013
 */
public interface Memory_Address_Book
{
    final short LAST_USER_MEMORY_ADDRESS = 3999;
    final short FIRST_TEMPORARY_MEMORY_ADDRESS = 4000;
    final short FIRST_OS_MEMORY_ADDRESS = 6000; //OS stands for operating sytem.
    final short LAST_OS_MEMORY_ADDRESS = 7999;
}

/* 
 * Array Index	Order of Items in the PCB 
        ↓                   ↓
        0           Next PCB pointer
        1                  PID
        2                 State
        3         Reason for waiting Code
        4                Priority
        5           Stack start address
        6               Stack size
        7       Message queue start address
        8           Message queue size
        9      Number of messages in the queue
        10                GPR 0 is status
        11 GPR 1 and the first memory address for tempory storage
        *  if the process has recieved storage.
        12                GPR 2 and the amount of tempory storge slots
        13                GPR 3
        14                GPR 4
        15                GPR 5
        16                GPR 6
        17                GPR 7
        18                  SP
        19                  PC
 */