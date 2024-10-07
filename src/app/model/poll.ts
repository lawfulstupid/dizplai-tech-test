import {PollOption} from "./poll-option";

export interface Poll {
    id: number;
    question: string;
    options: Array<PollOption>;
    userComplete?: boolean;
    totalVotes?: number;
    selectionMade?: boolean;
}