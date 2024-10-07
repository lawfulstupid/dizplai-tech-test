export interface PollOption {
    id: number;
    description: string;
    userSelection?: boolean;
    votes?: number;
    votesPercentage?: number;
}