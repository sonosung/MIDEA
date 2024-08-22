package org.astrologist.midea.service;

import org.astrologist.midea.dto.MindlistDTO;
import org.astrologist.midea.dto.PageRequestDTO;
import org.astrologist.midea.dto.PageResultDTO;
import org.astrologist.midea.entity.Mindlist;
import org.astrologist.midea.entity.User;

public interface MindlistService {
    Long register(MindlistDTO dto);

    PageResultDTO<MindlistDTO, Object[]> getList(PageRequestDTO requestDTO);

    MindlistDTO read(Long mno);

    void removeWithComments(Long mno);

    void modify(MindlistDTO dto);

    default Mindlist dtoToEntity(MindlistDTO dto) {

        User user = User.builder()
                .email(dto.getEmail())
                .nickname(dto.getNickname())
                .password(dto.getPassword())
                .build();

        Mindlist mindlist = Mindlist.builder()
                .mno(dto.getMno())
                .composer(dto.getComposer())
                .title(dto.getTitle())
                .url(dto.getUrl())
                .content(dto.getContent())
//                .nickname(user.getNickname())
//                .nickname(user.getNickname())
                .email(user)
                .calm(dto.isCalm())
                .happy(dto.isHappy())
                .joyful(dto.isJoyful())
                .energetic(dto.isEnergetic())
                .sad(dto.isSad())
                .stressed(dto.isStressed())
                .likeCount(dto.getLikeCount())
                .build();
        return mindlist;
    }



    default MindlistDTO entityToDTO(Mindlist mindlist, User user, Long commentCount){

        MindlistDTO mindlistDTO = MindlistDTO.builder()
                .mno(mindlist.getMno())
                .composer(mindlist.getComposer())
                .title(mindlist.getTitle())
                .url(mindlist.getUrl())
                .email(String.valueOf(user))
                .nickname(String.valueOf(user))
                .content(mindlist.getContent())
                .regDate(mindlist.getRegDate())
                .modDate(mindlist.getModDate())
                .build();

        return mindlistDTO;
    }

}

