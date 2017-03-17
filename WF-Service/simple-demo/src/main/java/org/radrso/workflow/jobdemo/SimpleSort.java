package org.radrso.workflow.jobdemo;


/**
 * Created by rao-mengnan on 2017/3/15.
 */
public class SimpleSort {

    public static Integer[] quickSort(Integer[] args){
        sort(args, 0, args.length - 1);
        return args;
    }

    private static void sort(Integer[] args,  int left, int right){
        int start = left;
        int end = right;
        if (start > end)
            return;

        int pivot = start;
        while (start != end) {
            for (; end > start && args[end] >= args[pivot]; --end) ;
            for (; start < end && args[start] <= args[pivot]; ++start) ;

            if(start < end){
                int swap = args[start];
                args[start] = args[end];
                args[end] = swap;
            }
        }

        int swap = args[pivot];
        args[pivot] = args[start];
        args[start] = swap;
        args[start] = args[end];
        pivot = start;

        sort(args, left, pivot - 1);
        sort(args, pivot + 1, right);
    }

}
