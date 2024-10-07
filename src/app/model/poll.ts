import {PollOption} from "./poll-option";

export interface Poll {
    id: number;
    name: string;
    question: string;
    options: Array<PollOption>;
    userComplete?: boolean;
    totalVotes?: number;
}