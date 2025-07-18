package com.busanit501.boot_project.service;

import com.busanit501.boot_project.domain.Board;
import com.busanit501.boot_project.domain.Reply;
import com.busanit501.boot_project.dto.PageRequestDTO;
import com.busanit501.boot_project.dto.PageResponseDTO;
import com.busanit501.boot_project.dto.ReplyDTO;
import com.busanit501.boot_project.repository.BoardRepository;
import com.busanit501.boot_project.repository.ReplyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class ReplyServiceImpl implements ReplyService {

    private final ReplyRepository replyRepository;
    private final ModelMapper modelMapper;
    private final BoardRepository boardRepository;

    @Override
    public Long register(ReplyDTO replyDTO) {
        log.info("ReplyServiceImpl 에서, 화면으로 부터 전달 받은 데이터 확인 replyDTO: " + replyDTO);
        Reply reply = modelMapper.map(replyDTO, Reply.class);

        Optional<Board> result = boardRepository.findById(replyDTO.getBno());
        Board board = result.orElseThrow();
        reply.changeBoard(board);
        log.info("ReplyServiceImpl 에서, 화면으로 부터 전달 받은 데이터 확인2 reply: " + reply);
        Long rno = replyRepository.save(reply).getRno();
        return rno;
    }

    @Override
    public ReplyDTO read(Long rno) {

        Optional<Reply> result = replyRepository.findById(rno);
        Reply reply = result.orElseThrow();
        log.info("ReplyServiceImpl 에서, read ,데이터 확인 reply: " + reply);

        ReplyDTO replyDTO = modelMapper.map(reply, ReplyDTO.class);
        replyDTO.setBno(reply.getBoard().getBno());
        log.info("ReplyServiceImpl 에서, read ,데이터 확인2 replyDTO: " + replyDTO);
        return replyDTO;
    }

    @Transactional
    @Override
    public void modify(ReplyDTO replyDTO) {
        log.info("ReplyServiceImpl 에서, modify ,데이터 확인 replyDTO: " + replyDTO);
        Reply reply = replyRepository.findById(replyDTO.getRno()).orElseThrow();
        log.info("ReplyServiceImpl 에서, modify ,데이터 확인2 reply: " + reply);
        reply.changeReplyText(replyDTO.getReplyText());

        reply.changeReplyer(replyDTO.getReplyer());

        log.info("ReplyServiceImpl 에서, modify ,데이터 확인3 reply: " + reply);

        replyRepository.save(reply);
    }

    @Override
    public void remove(Long rno) {
        Reply reply = replyRepository.findById(rno).orElseThrow();
        replyRepository.deleteById(rno);
    }

    @Override
    public PageResponseDTO<ReplyDTO> getListOfBoard(Long bno, PageRequestDTO pageRequestDTO) {

        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() <=0 ? 0 : pageRequestDTO.getPage()-1
                , pageRequestDTO.getSize(), Sort.by("rno").ascending());

        Page<Reply> result = replyRepository.listOfBoard(bno, pageable);
        List<ReplyDTO> dtoList = result.getContent().stream()
                .map(reply -> modelMapper.map(reply, ReplyDTO.class))
                .collect(Collectors.toList());

        return PageResponseDTO.<ReplyDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) result.getTotalElements())
                .build();
    }
}
