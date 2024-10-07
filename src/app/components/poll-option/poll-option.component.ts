import {Component, EventEmitter, Input, Output} from "@angular/core";
import {PollOption} from "../../model/poll-option";

@Component({
  selector: 'poll-option',
  templateUrl: 'poll-option.component.html',
  styleUrl: 'poll-option.component.scss'
})
export class PollOptionComponent {

  @Input() pollOption!: PollOption;

  @Input() showVotes: boolean = false;

  @Input() canSelect: boolean = false;

  @Output() onSelect: EventEmitter<PollOption | null> = new EventEmitter();

  select() {
    if (!this.pollOption.userSelection) {
      this.onSelect.emit(this.pollOption);
    } else {
      this.onSelect.emit(null);
    }
  }

}
