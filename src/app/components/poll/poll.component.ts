import {Component, EventEmitter, Input, Output} from "@angular/core";
import { Poll } from "../../model/poll";
import {PollOption} from "../../model/poll-option";

@Component({
  selector: 'poll',
  templateUrl: 'poll.component.html',
  styleUrl: 'poll.component.scss'
})
export class PollComponent {
  
  @Input() poll!: Poll;

  @Output() onSubmit: EventEmitter<number> = new EventEmitter();

  onOptionSelect(selectedOption: PollOption | null) {
    this.poll.options.forEach(pollOption => {
      pollOption.userSelection = pollOption.id === selectedOption?.id;
    });
    this.poll.selectionMade = this.poll.options.some(option => option.userSelection);
  }

}
